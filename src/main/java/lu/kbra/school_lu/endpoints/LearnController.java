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

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.school_lu.data.CurrentUser;
import lu.kbra.school_lu.data.ExamAttachmentType;
import lu.kbra.school_lu.data.ExamSeason;
import lu.kbra.school_lu.data.ExamType;
import lu.kbra.school_lu.data.ExerciseStatus;
import lu.kbra.school_lu.db.data.ExamData;
import lu.kbra.school_lu.db.data.ExerciseData;
import lu.kbra.school_lu.db.data.SubjectData;
import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.db.table.ExamAttachmentTable;
import lu.kbra.school_lu.db.table.ExamPartTable;
import lu.kbra.school_lu.db.table.ExamTable;
import lu.kbra.school_lu.db.table.ExerciseAttachmentTable;
import lu.kbra.school_lu.db.table.ExerciseTable;
import lu.kbra.school_lu.db.table.ExerciseTagTable;
import lu.kbra.school_lu.db.table.SectionTable;
import lu.kbra.school_lu.db.table.SubjectTable;
import lu.kbra.school_lu.db.table.TagTable;
import lu.kbra.school_lu.db.table.TagTable.TagProp;
import lu.kbra.school_lu.service.UserConfigService;
import lu.kbra.school_lu.service.UserService;

@RestController
@RequiredArgsConstructor
public class LearnController {

	private static final int LIMIT_EXERCISE_CHOICE = 5;
	private static final String LEARN_SAVED_STATE = "LEARN_SAVED_STATE";

	private final SectionTable sectionTable;
	private final SubjectTable subjectTable;
	private final ExamTable examTable;
	private final ExamPartTable examPartTable;
	private final ExamAttachmentTable examAttachmentTable;
	private final ExerciseTable exerciseTable;
	private final ExerciseAttachmentTable exerciseAttachmentTable;
	private final ExerciseTagTable exerciseTagTable;
	private final TagTable tagTable;

	private final UserService userService;
	private final UserConfigService userConfigService;

	private final ObjectMapper objectMapper;

	public record NextRequest(
			boolean withSolutionOnly,
			@NotEmpty Map<@NotBlank @NotNull String, @NotEmpty Set<@NotBlank @NotNull String>> subjects,
			Set<@NotBlank @NotNull String> requiredTags,
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

	@PutMapping("/learn/save")
	public void save(@CurrentUser final UserData userData, @RequestBody final NextRequest request) throws JsonProcessingException {
		this.userConfigService.setConfig(userData, LearnController.LEARN_SAVED_STATE, this.objectMapper.writeValueAsString(request));
	}

	@GetMapping("/learn/restore")
	public ResponseEntity<NextRequest> restore(@CurrentUser final UserData userData) throws JsonProcessingException {
		final String content = this.userConfigService.getConfig(userData, LearnController.LEARN_SAVED_STATE);
		final NextRequest result;
		if (content != null) {
			result = this.objectMapper.readValue(content, NextRequest.class);
		} else {
			result = null;
		}
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(result);
	}

	@PostMapping("/learn/next")
	public ResponseEntity<?> next(@CurrentUser final UserData userData, @RequestBody @Valid final NextRequest request) {
		final Set<SubjectData> subjects = request.subjects()
				.entrySet()
				.stream()
				.flatMap(section -> this.subjectTable.bySection(section.getKey(), section.getValue()).stream())
				.collect(Collectors.toSet());

		if (subjects.isEmpty()) {
			// TODO: better error
			return null;
		}

		final List<ExerciseData> exercises;
		if (request.withSolutionOnly()) {
			exercises = request.excludeSuccess()
					? this.exerciseTable.withSolutionAnySubjectAllTagsNotByStatus(subjects,
							request.requiredTags(),
							userData,
							ExerciseStatus.SUCCESS,
							LearnController.LIMIT_EXERCISE_CHOICE)
					: this.exerciseTable.withSolutionAnySubjectAllTagsNotByStatus(subjects,
							request.requiredTags(),
							userData,
							null,
							LearnController.LIMIT_EXERCISE_CHOICE);
		} else {
			exercises = request.excludeSuccess()
					? this.exerciseTable.byAnySubjectAllTagsNotByStatus(subjects,
							request.requiredTags(),
							userData,
							ExerciseStatus.SUCCESS,
							LearnController.LIMIT_EXERCISE_CHOICE)
					: this.exerciseTable.byAnySubjectAllTagsNotByStatus(subjects,
							request.requiredTags(),
							userData,
							null,
							LearnController.LIMIT_EXERCISE_CHOICE);
		}
		if (exercises.isEmpty()) {
			return this.whyNoResults(userData, subjects, request);
		}

		final List<Exercise> response = exercises.stream().map(c -> {
			final ExamData examData = this.examTable.byExercise(c);
			final String subjectName = this.subjectTable.nameByExam(examData);
			final String sectionName = this.sectionTable.nameByExam(examData);
			final String examAttachmentName = this.examPartTable.nameByExercise(c);
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

}
