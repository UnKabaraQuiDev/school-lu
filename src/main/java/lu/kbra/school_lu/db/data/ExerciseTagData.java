package lu.kbra.school_lu.db.data;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.domain.table.ForeignKeyData.OnAction;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.db.table.ExerciseTable;
import lu.kbra.school_lu.db.table.TagTable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class ExerciseTagData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@ForeignKey(table = ExerciseTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	private Long exerciseId;

	@Column
	@PrimaryKey
	@ForeignKey(table = TagTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	private Long tagId;

	public ExerciseTagData(final Long exerciseId, final Long tagId) {
		this.exerciseId = exerciseId;
		this.tagId = tagId;
	}

	@Override
	public ExerciseTagData clone() {
		return PCUtils.safeClone(super::clone);
	}

}
