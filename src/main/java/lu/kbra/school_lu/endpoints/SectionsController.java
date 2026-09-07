package lu.kbra.school_lu.endpoints;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lu.kbra.school_lu.db.table.SectionTable;
import lu.kbra.school_lu.db.table.SubjectTable;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/p")
@RequiredArgsConstructor
public class SectionsController {

	private final SectionTable sectionTable;
	private final SubjectTable subjectTable;

	@GetMapping("/sections")
	public List<String> sections() {
		return sectionTable.allNames();
	}

	@GetMapping("/subjects")
	public List<String> subjects() {
		return subjectTable.allNames();
	}

	@GetMapping("/subjects/{section}")
	public List<String> subjects(@PathVariable String section) {
		return subjectTable.allNames(section);
	}

}
