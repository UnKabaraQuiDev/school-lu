package lu.kbra.school_lu.db.data;


import lombok.Data;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;

@Data
public class SectionData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@Unique(1)
	@MaxLength(150)
	private String name;

	public SectionData(Long id) {
		this.id = id;
	}

	public SectionData(String name) {
		this.name = name;
	}

	@Override
	public SectionData clone() {
		return PCUtils.safeClone(super::clone);
	}
}