package lu.kbra.school_lu.endpoints;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.pclib.PCUtils;
import lu.kbra.school_lu.data.CurrentUser;
import lu.kbra.school_lu.data.ExerciseStatus;
import lu.kbra.school_lu.db.data.ExamData;
import lu.kbra.school_lu.db.data.ExerciseData;
import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.db.data.UserExerciseData;
import lu.kbra.school_lu.db.table.ExamPartTable;
import lu.kbra.school_lu.db.table.ExamTable;
import lu.kbra.school_lu.db.table.ExerciseTable;
import lu.kbra.school_lu.db.table.SectionTable;
import lu.kbra.school_lu.db.table.SubjectTable;
import lu.kbra.school_lu.db.table.TagTable;
import lu.kbra.school_lu.db.table.UserExerciseTable;
import lu.kbra.school_lu.endpoints.ReturnTypes.Exam;
import lu.kbra.school_lu.endpoints.ReturnTypes.ExerciseHistory;
import lu.kbra.school_lu.endpoints.ReturnTypes.Tag;
import lu.kbra.school_lu.service.UserConfigService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HistoryController {

	private static final String PAGE_SAVED_SIZE = "PAGE_SAVED_STATE";
	private static final int DEFAULT_PAGE_SIZE = 25;
	private static final String FILTERS_SAVED_STATE = "FILTERS_SAVED_STATE";

	private final UserExerciseTable userExerciseTable;
	private final ExerciseTable exerciseTable;
	private final ExamTable examTable;
	private final ExamPartTable examPartTable;
	private final SectionTable sectionTable;
	private final SubjectTable subjectTable;
	private final TagTable tagTable;

	private final UserConfigService userConfigService;

	private final ObjectMapper objectMapper;

	@PutMapping("/history/page-size")
	public void setPageSize(@CurrentUser final UserData userData, @Positive @RequestParam final int size) {
		this.userConfigService.setConfig(userData, HistoryController.PAGE_SAVED_SIZE, Integer.toString(size));
	}

	@GetMapping("/history/page-size")
	public ResponseEntity<?> getPageSize(@CurrentUser final UserData userData) {
		final String content = this.userConfigService.getConfig(userData, HistoryController.PAGE_SAVED_SIZE);
		return ResponseEntity.ok()
				.cacheControl(CacheControl.noStore())
				.body(PCUtils.parseInteger(content, HistoryController.DEFAULT_PAGE_SIZE));
	}

	@PutMapping("/history/filter")
	public void setFilter(@CurrentUser final UserData userData, @RequestParam final Set<@NotNull ExerciseStatus> status)
			throws JsonProcessingException {
		this.userConfigService.setConfig(userData, HistoryController.FILTERS_SAVED_STATE, this.objectMapper.writeValueAsString(status));
	}

	@GetMapping("/history/filter")
	public ResponseEntity<?> getFilter(@CurrentUser final UserData userData) {
		final String content = this.userConfigService.getConfig(userData, HistoryController.FILTERS_SAVED_STATE);
		Set<ExerciseStatus> result;
		if (content != null) {
			try {
				result = this.objectMapper.readValue(content, new TypeReference<Set<ExerciseStatus>>() {
				});
			} catch (JsonProcessingException e) {
				result = EnumSet.allOf(ExerciseStatus.class);
			}
		} else {
			result = EnumSet.allOf(ExerciseStatus.class);
		}
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(result);
	}

	@GetMapping("/history/page/{page}/{pageSize}")
	public List<ExerciseHistory> historyPage(
			@CurrentUser final UserData userData,
			@Positive @PathVariable final int page,
			@Positive @PathVariable final int pageSize,
			@NotNull @NotEmpty @RequestParam final Set<ExerciseStatus> status) {

		return this.userExerciseTable.byUserAndOffsetAndLimitAnyStatus(userData, status, (page - 1) * pageSize, pageSize)
				.stream()
				.map(o -> {
					final ExerciseData exercise = this.exerciseTable.byId(o.getExerciseId()).get();
					final ExamData examData = this.examTable.byExercise(exercise);

					final String subjectName = this.subjectTable.nameByExam(examData);
					final String sectionName = this.sectionTable.nameByExam(examData);
					final String examPartName = this.examPartTable.nameByExercise(exercise);

					final List<Tag> tags = this.tagTable.byExercise(exercise)
							.stream()
							.map(t -> new Tag(t.getColor(), t.getName()))
							.toList();

					return new ExerciseHistory(
							new Exam(sectionName,
									subjectName,
									examData.getYear(),
									examData.getSeason(),
									examData.getSubtype(),
									examPartName),
							exercise.getId(),
							exercise.getName(),
							exercise.getExerciseIndex(),
							tags,
							o.getStatus(),
							o.getTimestamp());
				})
				.toList();
	}

	@PatchMapping("/history/remove/{exerciseId}")
	public ResponseEntity<?> removeExercise(@CurrentUser final UserData userData, @Positive @PathVariable final long exerciseId) {
		return this.userExerciseTable.deleteIfExists(new UserExerciseData(userData.getId(), exerciseId)).isPresent()
				? ResponseEntity.accepted().build()
				: ResponseEntity.noContent().build();
	}

	@PatchMapping("/history/remove")
	public ResponseEntity<Integer>
			removeExercises(@CurrentUser final UserData userData, @RequestBody @Valid final List<@Positive Long> exerciseIds) {
		final List<UserExerciseData> exercises = exerciseIds.stream()
				.map(exerciseId -> new UserExerciseData(userData.getId(), exerciseId))
				.toList();

		final List<UserExerciseData> existingExercises = this.userExerciseTable.filterExists(exercises, ArrayList::new);

		if (existingExercises.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		this.userExerciseTable.deleteAll(existingExercises);

		return ResponseEntity.ok(existingExercises.size());
	}

	@GetMapping("/history/count")
	public ResponseEntity<?>
			historyCount(@CurrentUser final UserData userData, @NotNull @NotEmpty @RequestParam final Set<ExerciseStatus> status) {
		return ResponseEntity.ok(this.userExerciseTable.countByUserAnyStatus(userData, status));
	}

}
