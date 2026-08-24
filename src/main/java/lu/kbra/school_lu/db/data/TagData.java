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
public class TagData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@Unique(1)
	@MaxLength(100)
	private String name;

	public TagData(Long id) {
		this.id = id;
	}

	public TagData(String name) {
		this.name = name;
	}

	@Override
	public TagData clone() {
		return PCUtils.safeClone(super::clone);
	}
}