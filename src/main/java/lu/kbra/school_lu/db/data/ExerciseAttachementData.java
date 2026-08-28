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
import lu.kbra.school_lu.db.table.ExerciseTable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExerciseAttachementData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@Unique(1)
	@ForeignKey(table = ExerciseTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	private Long exerciseId;

	@Column
	@Unique(1)
	@MaxLength(64)
	private String qualifier;

	@Column
	@Unique(2)
	private String location;

	public ExerciseAttachementData(Long id) {
		this.id = id;
	}

	public ExerciseAttachementData(Long exerciseId, String qualifier) {
		this.exerciseId = exerciseId;
		this.qualifier = qualifier;
	}

	public ExerciseAttachementData(Long exerciseId, String qualifier, String location) {
		this.exerciseId = exerciseId;
		this.qualifier = qualifier;
		this.location = location;
	}

	@Override
	public ExerciseAttachementData clone() {
		return PCUtils.safeClone(super::clone);
	}

}
