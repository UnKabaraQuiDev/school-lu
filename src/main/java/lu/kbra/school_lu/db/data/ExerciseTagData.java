package lu.kbra.school_lu.db.data;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.db.table.ExerciseTable;
import lu.kbra.school_lu.db.table.TagTable;

import lombok.Data;

@Data
public class ExerciseTagData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = ExerciseTable.class)
	@Unique(1)
	private Long exerciseId;

	@Column
	@ForeignKey(table = TagTable.class)
	@Unique(1)
	private Long tagId;

	public ExerciseTagData(Long id) {
		this.id = id;
	}

	public ExerciseTagData(Long exerciseId, Long tagId) {
		this.exerciseId = exerciseId;
		this.tagId = tagId;
	}

	@Override
	public ExerciseTagData clone() {
		return PCUtils.safeClone(super::clone);
	}

}
