package lu.kbra.school_lu.endpoints;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lu.kbra.school_lu.db.table.SectionTable;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sections")
@RequiredArgsConstructor
public class SectionsController {

	private final SectionTable sectionTable;

	@GetMapping("/list")
	public List<String> sections() {
		return sectionTable.allNames();
	}

}
