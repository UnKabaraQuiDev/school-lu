package lu.kbra.school_lu.endpoints;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lu.kbra.school_lu.db.data.SectionData;
import lu.kbra.school_lu.db.table.SectionTable;
import lu.kbra.school_lu.db.table.SubjectTable;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectsController {

	private final SectionTable sectionTable;
	private final SubjectTable subjectTable;

	@GetMapping("/list")
	public List<String> subjects() {
		return subjectTable.allNames();
	}

	@GetMapping("/tree")
	public Map<String, List<String>> tree() {
		return sectionTable.all()
				.stream()
				.sorted(Comparator.comparing(SectionData::getName))
				.collect(Collectors.toMap(SectionData::getName,
						section -> subjectTable.allNames(section.getName()).stream().sorted().toList(),
						(a, b) -> a,
						LinkedHashMap::new));
	}

	@GetMapping("/{section}")
	public List<String> subjects(@PathVariable String section) {
		return subjectTable.allNames(section);
	}

}
