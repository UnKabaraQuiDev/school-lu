package lu.kbra.school_lu.endpoints;

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
import lu.kbra.pclib.PCUtils;
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
import lu.kbra.school_lu.service.UserService;

@RestController
@RequiredArgsConstructor
public class LearnController {

	private final SectionTable sectionTable;
	private final SubjectTable subjectTable;
	private final ExamTable examTable;
	private final ExamAttachmentTable examAttachmentTable;
	private final ExerciseTable exerciseTable;
	private final ExerciseAttachmentTable exerciseAttachmentTable;
	private final ExerciseTagTable exerciseTagTable;
	private final TagTable tagTable;
	private final UserService userService;

	public record NextRequest(boolean withSolutionOnly, Map<String, Set<String>> subjects, Set<String> requiredTags) {
	}

	public record Exam(String section, String subject, int year, ExamSeason season, ExamType subtype, String name) {
	}

	public record Exercise(Exam exam, int exerciseIndex, List<ExerciseAttachment> attachments, List<Tag> tags) {
	}

	public record ExerciseAttachment(ExamAttachmentType qualifier, String location) {
	}

	public record Tag(int color, String name) {
	}

	public record ErrorBody(String message, Object obj) {
	}

	@PostMapping("/learn/next")
	public ResponseEntity<?> next(@AuthenticationPrincipal final UserId principal, @RequestBody final NextRequest request) {
		final UserData userData = userService.get(principal);
		final Set<SubjectData> subjects = request.subjects()
				.entrySet()
				.stream()
				.flatMap(section -> this.subjectTable.bySection(section.getKey(), section.getValue()).stream())
				.collect(Collectors.toSet());
		final List<ExerciseData> exercises = request.withSolutionOnly()
				? this.exerciseTable
						.withSolutionAnySubjectAllTagsNotByStatus(subjects, request.requiredTags(), userData, ExerciseStatus.SUCCESS, 5)
				: this.exerciseTable.byAnySubjectAllTagsNotByStatus(subjects, request.requiredTags(), userData, ExerciseStatus.SUCCESS, 5);
		if (exercises.isEmpty()) {
			if (!request.requiredTags().isEmpty()) {
				final List<String> foundTags = this.tagTable.byName(request.requiredTags());
				System.err.println("found " + foundTags);
				request.requiredTags().removeAll(foundTags);
				if (!request.requiredTags().isEmpty()) {
					return this.InvalidTags(request.requiredTags());
				}
			}

			return ResponseEntity.noContent().build();
		}

		final List<Exercise> response = exercises.stream().map(c -> {
			final ExamData examData = this.examTable.byExercise(c);
			final String subjectName = this.subjectTable.nameByExam(examData);
			final String sectionName = this.sectionTable.nameByExam(examData);
			final String examAttachmentName = this.examAttachmentTable.nameByExerciseAttachment(c);
			final List<ExerciseAttachment> attachs = exerciseAttachmentTable.byExercise(c)
					.stream()
					.map(t -> new ExerciseAttachment(t.getQualifier(), t.getLocation()))
					.toList();
			final List<Tag> tags = tagTable.byExercise(c).stream().map(t -> new Tag(t.getColor(), t.getName())).toList();
			return new Exercise(
					new Exam(sectionName, subjectName, examData.getYear(), examData.getSeason(), examData.getSubtype(), examAttachmentName),
					c.getExerciseIndex(),
					attachs,
					tags);
		}).toList();

		return ResponseEntity.ok(response);
	}

	private ResponseEntity<?> InvalidTags(final Set<String> invalidTags) {
		return ResponseEntity.badRequest().body(new ErrorBody("Invalid tags.", invalidTags));
	}

	@GetMapping("/p/test")
	public ResponseEntity<?> test() {
		return this.next(new UserId(1L),
				new NextRequest(false, PCUtils.hashMap("CA", PCUtils.hashSet("MATHE")), PCUtils.hashSet("eq_log")));
	}

}
