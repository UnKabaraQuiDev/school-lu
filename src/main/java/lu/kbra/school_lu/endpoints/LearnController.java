package lu.kbra.school_lu.endpoints;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lu.kbra.school_lu.data.UserId;
import lu.kbra.school_lu.db.data.ExerciseData;
import lu.kbra.school_lu.db.table.ExamAttachmentTable;
import lu.kbra.school_lu.db.table.ExamTable;
import lu.kbra.school_lu.db.table.ExerciseAttachmentTable;
import lu.kbra.school_lu.db.table.ExerciseTable;
import lu.kbra.school_lu.db.table.ExerciseTagTable;
import lu.kbra.school_lu.db.table.SectionTable;
import lu.kbra.school_lu.db.table.SubjectTable;
import lu.kbra.school_lu.db.table.TagTable;

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

	@PostMapping("/learn/next")
	public void next(@AuthenticationPrincipal UserId principal, @RequestBody NextRequest request) {

	}

	@GetMapping("/p/test")
	public List<ExerciseData> test() {
		return exerciseTable.withSolutionAnySubjectAnyTag(subjectTable.all(), tagTable.all());
	}

}
