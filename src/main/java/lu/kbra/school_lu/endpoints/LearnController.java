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
import java.util.function.ToIntFunction;
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

import lu.kbra.school_lu.data.CurrentUser;
import lu.kbra.school_lu.data.ExerciseStatus;
import lu.kbra.school_lu.db.data.ExamData;
import lu.kbra.school_lu.db.data.ExerciseData;
import lu.kbra.school_lu.db.data.SubjectData;
import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.db.data.UserExerciseData;
import lu.kbra.school_lu.db.table.ExamPartTable;
import lu.kbra.school_lu.db.table.ExamTable;
import lu.kbra.school_lu.db.table.ExerciseAttachmentTable;
import lu.kbra.school_lu.db.table.ExerciseTable;
import lu.kbra.school_lu.db.table.SectionTable;
import lu.kbra.school_lu.db.table.SubjectTable;
import lu.kbra.school_lu.db.table.TagTable;
import lu.kbra.school_lu.db.table.TagTable.TagProp;
import lu.kbra.school_lu.db.table.UserExerciseTable;
import lu.kbra.school_lu.endpoints.ReturnTypes.ErrorBody;
import lu.kbra.school_lu.endpoints.ReturnTypes.Exam;
import lu.kbra.school_lu.endpoints.ReturnTypes.Exercise;
import lu.kbra.school_lu.endpoints.ReturnTypes.ExerciseAttachment;
import lu.kbra.school_lu.endpoints.ReturnTypes.NextExercise;
import lu.kbra.school_lu.endpoints.ReturnTypes.Tag;
import lu.kbra.school_lu.endpoints.ReturnTypes.TagBottleneck;
import lu.kbra.school_lu.endpoints.ReturnTypes.YearRange;
import lu.kbra.school_lu.service.UserConfigService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LearnController {

	private static final int LIMIT_EXERCISE_CHOICE = 1;
	private static final String LEARN_SAVED_STATE = "LEARN_SAVED_STATE";

	private final SectionTable sectionTable;
	private final SubjectTable subjectTable;
	private final ExamTable examTable;
	private final ExamPartTable examPartTable;
	private final ExerciseTable exerciseTable;
	private final ExerciseAttachmentTable exerciseAttachmentTable;
	private final TagTable tagTable;
	private final UserExerciseTable userExerciseTable;

	private final UserConfigService userConfigService;

	private final ObjectMapper objectMapper;

	@PutMapping("/learn/skip")
	public ResponseEntity<?> skip(@CurrentUser final UserData userData, @RequestBody @Valid @Positive final int exerciseId) {
		return this.setStatus(userData, exerciseId, ExerciseStatus.SKIP);
	}

	@PutMapping("/learn/success")
	public ResponseEntity<?> success(@CurrentUser final UserData userData, @RequestBody @Valid @Positive final int exerciseId) {
		return this.setStatus(userData, exerciseId, ExerciseStatus.SUCCESS);
	}

	@PutMapping("/learn/failed")
	public ResponseEntity<?> failed(@CurrentUser final UserData userData, @RequestBody @Valid @Positive final int exerciseId) {
		return this.setStatus(userData, exerciseId, ExerciseStatus.FAILED);
	}

	@PutMapping("/learn/save")
	public void save(@CurrentUser final UserData userData, @RequestBody final NextExercise request) throws JsonProcessingException {
		this.userConfigService.setConfig(userData, LearnController.LEARN_SAVED_STATE, this.objectMapper.writeValueAsString(request));
	}

	@GetMapping("/learn/restore")
	public ResponseEntity<NextExercise> restore(@CurrentUser final UserData userData) throws JsonProcessingException {
		final String content = this.userConfigService.getConfig(userData, LearnController.LEARN_SAVED_STATE);
		final NextExercise result;
		if (content != null) {
			result = this.objectMapper.readValue(content, NextExercise.class);
		} else {
			result = null;
		}
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(result);
	}

	@PostMapping("/learn/next")
	public ResponseEntity<?> next(@CurrentUser final UserData userData, @RequestBody @Valid final NextExercise request) {
		final Set<SubjectData> subjects = request.subjects()
				.entrySet()
				.stream()
				.flatMap(section -> this.subjectTable.bySection(section.getKey(), section.getValue()).stream())
				.collect(Collectors.toSet());

		if (subjects.isEmpty()) {
			return ResponseEntity.badRequest().body(new ErrorBody("Subjects not found.", request.subjects()));
		}

		final YearRange yearRange = request.yearRange() == null ? new YearRange(0, 3000)
				: new YearRange(Math.min(request.yearRange().from(), request.yearRange().to()),
						Math.max(request.yearRange().from(), request.yearRange().to()));

		final List<ExerciseData> exercises;
		if (request.withSolutionOnly()) {
			exercises = this.exerciseTable.withSolutionAnySubjectAllTagsNotStatus(subjects,
					request.requiredTags(),
					userData,
					request.excludeSuccess() ? ExerciseStatus.SUCCESS : null,
					yearRange.from(),
					yearRange.to(),
					LearnController.LIMIT_EXERCISE_CHOICE);
		} else {
			exercises = this.exerciseTable.byAnySubjectAllTagsNotStatus(subjects,
					request.requiredTags(),
					userData,
					request.excludeSuccess() ? ExerciseStatus.SUCCESS : null,
					yearRange.from(),
					yearRange.to(),
					LearnController.LIMIT_EXERCISE_CHOICE);
		}
		if (exercises.isEmpty()) {
			return this.whyNoResults(userData, subjects, yearRange, request);
		}

		final List<Exercise> response = exercises.stream().map(c -> {
			final ExamData examData = this.examTable.byExercise(c);
			final String subjectName = this.subjectTable.nameByExam(examData);
			final String sectionName = this.sectionTable.nameByExam(examData);
			final String examAttachmentName = this.examPartTable.nameByExercise(c);
			final List<ExerciseAttachment> attachs = this.exerciseAttachmentTable.byExercise(c)
					.stream()
					.map(t -> new ExerciseAttachment(t.getQualifier(), t.getLocation()))
					.sorted(Comparator.comparingInt(t -> t.qualifier().ordinal()))
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

	private ResponseEntity<?>
			whyNoResults(final UserData userData, final Set<SubjectData> subjects, final YearRange yearRange, final NextExercise request) {
		if (!request.requiredTags().isEmpty()) {
			final List<String> foundTags = this.tagTable.byName(request.requiredTags());
			final Set<String> requiredTags = new HashSet<>(request.requiredTags());
			requiredTags.removeAll(foundTags);
			if (!requiredTags.isEmpty()) {
				return ResponseEntity.badRequest().body(new ErrorBody("Invalid tags.", requiredTags));
			}
		}

		if (request.requiredTags().isEmpty()) {
			return ResponseEntity.badRequest()
					.body(new ErrorBody("Year range probably too restrictive.",
							request.withSolutionOnly()
									? exerciseTable.countWithSolutionAnySubjectAllTagsNotStatusByYear(subjects,
											request.requiredTags(),
											userData,
											request.excludeSuccess() ? ExerciseStatus.SUCCESS : null)
									: exerciseTable.countByAnySubjectAllTagsNotStatusByYear(subjects,
											request.requiredTags(),
											userData,
											request.excludeSuccess() ? ExerciseStatus.SUCCESS : null)));
		}

		final List<TagProp> tagProps = this.tagTable.propByName(request.requiredTags());
		tagProps.sort(Comparator.comparing(c -> -c.count()));

		final Set<String> usingTags = new HashSet<>();
		final Map<String, Integer> counts = new HashMap<>();

		final Iterator<TagProp> it = tagProps.iterator();

		final ToIntFunction<Set<String>> countFor = tags -> {
			if (request.withSolutionOnly()) {
				return this.exerciseTable.countWithSolutionAnySubjectAllTagsNotStatus(subjects,
						tags,
						userData,
						request.excludeSuccess() ? ExerciseStatus.SUCCESS : null,
						yearRange.from(),
						yearRange.to());
			} else {
				return this.exerciseTable.countByAnySubjectAllTagsNotStatus(subjects,
						tags,
						userData,
						request.excludeSuccess() ? ExerciseStatus.SUCCESS : null,
						yearRange.from(),
						yearRange.to());
			}
		};

		while (it.hasNext()) {
			final String newTag = it.next().name();

			usingTags.add(newTag);

			final int count = countFor.applyAsInt(usingTags);

			counts.put(newTag, count);
		}

		final List<TagBottleneck> bottlenecks = new ArrayList<>();

		int previousCount = countFor.applyAsInt(Collections.EMPTY_SET);
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

	private ResponseEntity<?> setStatus(final UserData userData, final long exerciseId, final ExerciseStatus status) {
		if (!this.exerciseTable.exists(exerciseId)) {
			return ResponseEntity.badRequest().body(new ErrorBody("Invalid exercise id.", exerciseId));
		}
		this.userExerciseTable.updateIfExistsElseInsert(new UserExerciseData(userData.getId(), exerciseId, status));
		return ResponseEntity.accepted().build();
	}

}
