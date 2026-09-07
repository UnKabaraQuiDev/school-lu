package lu.kbra.school_lu.endpoints;

import java.util.HashSet;
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

import lu.kbra.school_lu.data.UserId;
import lu.kbra.school_lu.db.data.ExerciseData;
import lu.kbra.school_lu.db.data.SubjectData;
import lu.kbra.school_lu.db.table.ExamAttachmentTable;
import lu.kbra.school_lu.db.table.ExamTable;
import lu.kbra.school_lu.db.table.ExerciseAttachmentTable;
import lu.kbra.school_lu.db.table.ExerciseTable;
import lu.kbra.school_lu.db.table.ExerciseTagTable;
import lu.kbra.school_lu.db.table.SectionTable;
import lu.kbra.school_lu.db.table.SubjectTable;
import lu.kbra.school_lu.db.table.TagTable;

import lombok.RequiredArgsConstructor;

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

	public record NextRequest(boolean withSolutionOnly, Map<String, Set<String>> subjects, Set<String> requiredTags) {
	}

	public record ErrorBody(String message, Object obj) {
	}

	@PostMapping("/learn/next")
	public ResponseEntity<?> next(@AuthenticationPrincipal UserId principal, @RequestBody NextRequest request) {
		final Set<SubjectData> subjects = request.subjects()
				.entrySet()
				.stream()
				.flatMap(section -> subjectTable.bySection(section.getKey(), section.getValue()).stream())
				.collect(Collectors.toSet());
		final List<ExerciseData> exercises = exerciseTable.withSolutionAnySubjectAllTags(subjects, request.requiredTags(), 5);
		if (exercises.isEmpty()) {
			final List<String> foundTags = tagTable.byName(request.requiredTags());
			request.requiredTags().removeAll(foundTags);
			if (!request.requiredTags().isEmpty()) {
				return InvalidTags(request.requiredTags());
			}

			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok().build();
	}

	private ResponseEntity<?> InvalidTags(Set<String> invalidTags) {
		return ResponseEntity.badRequest().body(new ErrorBody("Invalid tags.", invalidTags));
	}

	@GetMapping("/p/test")
	public List<ExerciseData> test() {
		return exerciseTable.withSolutionAnySubjectAllTags(subjectTable.all(), new HashSet<>(), 1);
	}

}
