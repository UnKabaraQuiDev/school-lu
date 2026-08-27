package lu.kbra.school_lu.db.data;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.domain.table.ForeignKeyData.OnAction;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.db.table.SectionTable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubjectData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = SectionTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	@Unique(1)
	private Long sectionId;

	@Column
	@Unique(1)
	@MaxLength(48)
	private String name;

	public SubjectData(final Long id) {
		this.id = id;
	}

	public SubjectData(final Long sectionId, final String name) {
		this.sectionId = sectionId;
		this.name = name;
	}

	@Override
	public SubjectData clone() {
		return PCUtils.safeClone(super::clone);
	}
}
