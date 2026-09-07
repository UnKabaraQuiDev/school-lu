package lu.kbra.school_lu.endpoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lu.kbra.school_lu.data.ExamAttachmentType;
import lu.kbra.school_lu.data.ExamSeason;
import lu.kbra.school_lu.data.ExamType;
import lu.kbra.school_lu.data.ExerciseStatus;
import lu.kbra.school_lu.data.UserId;
import lu.kbra.school_lu.db.data.ExamData;
import lu.kbra.school_lu.db.data.ExerciseData;
import lu.kbra.school_lu.db.data.SubjectData;
import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.db.table.ExamAttachmentTable;
import lu.kbra.school_lu.db.table.ExamTable;
import lu.kbra.school_lu.db.table.ExerciseAttachmentTable;
import lu.kbra.school_lu.db.table.ExerciseTable;
import lu.kbra.school_lu.db.table.ExerciseTagTable;
import lu.kbra.school_lu.db.table.SectionTable;
import lu.kbra.school_lu.db.table.SubjectTable;
import lu.kbra.school_lu.db.table.TagTable;
import lu.kbra.school_lu.db.table.TagTable.TagProp;
import lu.kbra.school_lu.service.UserService;

@RestController
@RequiredArgsConstructor
public class LearnController {

	private static final int LIMIT_EXERCISE_CHOICE = 5;
	private final SectionTable sectionTable;
	private final SubjectTable subjectTable;
	private final ExamTable examTable;
	private final ExamAttachmentTable examAttachmentTable;
	private final ExerciseTable exerciseTable;
	private final ExerciseAttachmentTable exerciseAttachmentTable;
	private final ExerciseTagTable exerciseTagTable;
	private final TagTable tagTable;
	private final UserService userService;

	public record NextRequest(
			boolean withSolutionOnly,
			Map<String, Set<String>> subjects,
			Set<String> requiredTags,
			boolean excludeSuccess) {
	}

	public record Exam(String section, String subject, int year, ExamSeason season, ExamType subtype, String name) {
	}

	public record Exercise(Exam exam, long id, int exerciseIndex, List<ExerciseAttachment> attachments, List<Tag> tags) {
	}

	public record ExerciseAttachment(ExamAttachmentType qualifier, String location) {
	}

	public record Tag(int color, String name) {
	}

	public record ErrorBody(String message, Object obj) {
	}

	public record TagBottleneck(String name, int count, int negativeCount, double negativePercentage) {
	}

	@PostMapping("/learn/next")
	public ResponseEntity<?> next(@AuthenticationPrincipal final UserId userId, @RequestBody final NextRequest request) {
		final UserData userData = this.userService.get(userId);
		final Set<SubjectData> subjects = request.subjects()
				.entrySet()
				.stream()
				.flatMap(section -> this.subjectTable.bySection(section.getKey(), section.getValue()).stream())
				.collect(Collectors.toSet());

		final List<ExerciseData> exercises;
		if (request.withSolutionOnly()) {
			exercises = request.excludeSuccess()
					? this.exerciseTable.withSolutionAnySubjectAllTagsNotByStatus(subjects,
							request.requiredTags(),
							userData,
							ExerciseStatus.SUCCESS,
							LIMIT_EXERCISE_CHOICE)
					: this.exerciseTable.withSolutionAnySubjectAllTagsNotByStatus(subjects,
							request.requiredTags(),
							userData,
							null,
							LIMIT_EXERCISE_CHOICE);
		} else {
			exercises = request.excludeSuccess()
					? this.exerciseTable.byAnySubjectAllTagsNotByStatus(subjects,
							request.requiredTags(),
							userData,
							ExerciseStatus.SUCCESS,
							LIMIT_EXERCISE_CHOICE)
					: this.exerciseTable
							.byAnySubjectAllTagsNotByStatus(subjects, request.requiredTags(), userData, null, LIMIT_EXERCISE_CHOICE);
		}
		if (exercises.isEmpty()) {
			return this.whyNoResults(userData, subjects, request);
		}

		final List<Exercise> response = exercises.stream().map(c -> {
			final ExamData examData = this.examTable.byExercise(c);
			final String subjectName = this.subjectTable.nameByExam(examData);
			final String sectionName = this.sectionTable.nameByExam(examData);
			final String examAttachmentName = this.examAttachmentTable.nameByExerciseAttachment(c);
			final List<ExerciseAttachment> attachs = this.exerciseAttachmentTable.byExercise(c)
					.stream()
					.map(t -> new ExerciseAttachment(t.getQualifier(), t.getLocation()))
					.toList();
			final List<Tag> tags = this.tagTable.byExercise(c).stream().map(t -> new Tag(t.getColor(), t.getName())).toList();
			return new Exercise(
					new Exam(sectionName, subjectName, examData.getYear(), examData.getSeason(), examData.getSubtype(), examAttachmentName),
					c.getId(),
					c.getExerciseIndex(),
					attachs,
					tags);
		}).toList();

		return ResponseEntity.ok(response);
	}

	private ResponseEntity<?> whyNoResults(final UserData userData, final Set<SubjectData> subjects, final NextRequest request) {
		if (!request.requiredTags().isEmpty()) {
			final List<String> foundTags = this.tagTable.byName(request.requiredTags());
			final Set<String> requiredTags = new HashSet<>(request.requiredTags());
			requiredTags.removeAll(foundTags);
			if (!requiredTags.isEmpty()) {
				return ResponseEntity.badRequest().body(new ErrorBody("Invalid tags.", requiredTags));
			}
		}

		final List<TagProp> tagProps = this.tagTable.propByName(request.requiredTags());
		tagProps.sort(Comparator.comparing(c -> -c.count()));

		final Set<String> usingTags = new HashSet<>();
		final Map<String, Integer> counts = new HashMap<>();

		final Iterator<TagProp> it = tagProps.iterator();

		while (it.hasNext()) {
			final String newTag = it.next().name();

			usingTags.add(newTag);

			final int count;
			if (request.withSolutionOnly()) {
				count = request.excludeSuccess()
						? this.exerciseTable
								.countWithSolutionAnySubjectAllTagsNotByStatus(subjects, usingTags, userData, ExerciseStatus.SUCCESS)
						: this.exerciseTable.countWithSolutionAnySubjectAllTags(subjects, usingTags);
			} else {
				count = request.excludeSuccess()
						? this.exerciseTable.countByAnySubjectAllTagsNotByStatus(subjects, usingTags, userData, ExerciseStatus.SUCCESS)
						: this.exerciseTable.countByAnySubjectAllTags(subjects, usingTags);
			}

			counts.put(newTag, count);
		}

		final List<TagBottleneck> bottlenecks = new ArrayList<>();

		int previousCount;
		if (request.withSolutionOnly()) {
			previousCount = request.excludeSuccess() ? this.exerciseTable
					.countWithSolutionAnySubjectAllTagsNotByStatus(subjects, Collections.EMPTY_SET, userData, ExerciseStatus.SUCCESS)
					: this.exerciseTable.countWithSolutionAnySubjectAllTagsNotByStatus(subjects, Collections.EMPTY_SET, userData, null);
		} else {
			previousCount = request.excludeSuccess()
					? this.exerciseTable
							.countByAnySubjectAllTagsNotByStatus(subjects, Collections.EMPTY_SET, userData, ExerciseStatus.SUCCESS)
					: this.exerciseTable.countByAnySubjectAllTagsNotByStatus(subjects, Collections.EMPTY_SET, userData, null);
		}
		System.err.println(previousCount);

		bottlenecks.add(new TagBottleneck("", previousCount, 0, 0));

		for (final TagProp tag : tagProps) {
			final int count = counts.get(tag.name());
			final int negativeCount = previousCount - count;
			final double negativePercentage = previousCount == 0 ? 0.0 : negativeCount * 100.0 / previousCount;

			bottlenecks.add(new TagBottleneck(tag.name(), count, negativeCount, negativePercentage));

			previousCount = count;
		}
		bottlenecks.sort(Comparator.comparing(c -> -c.count()));

		return ResponseEntity.badRequest().body(new ErrorBody("Tags too restrictive.", bottlenecks));
	}

	@GetMapping("/p/test")
	public Object test(@RequestBody NextRequest request) {
		return this.next(new UserId(1L), request);
//				new NextRequest(false,
//						PCUtils.hashMap("CA", PCUtils.hashSet("MATHE")),
//						PCUtils.hashSet("eq_log", "graphique", "probleme"),
//						false));
//		return this.tagTable.propByName(Set.of("eq_log", "graphique"));
	}

}
