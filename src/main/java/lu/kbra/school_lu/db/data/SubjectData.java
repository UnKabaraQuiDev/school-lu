package lu.kbra.school_lu.db.data;

import lombok.Data;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.db.table.SectionTable;

@Data
public class SubjectData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = SectionTable.class)
	@Unique(1)
	private Long sectionId;

	@Column
	@Unique(1)
	@MaxLength(150)
	private String name;

	public SubjectData(Long id) {
		this.id = id;
	}

	public SubjectData(Long sectionId, String name) {
		this.sectionId = sectionId;
		this.name = name;
	}

	@Override
	public SubjectData clone() {
		return PCUtils.safeClone(super::clone);
	}
}