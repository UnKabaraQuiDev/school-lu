package lu.kbra.school_lu.db.data;

import java.time.Instant;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.DefaultValue;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.domain.table.ForeignKeyData.OnAction;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.data.ExerciseStatus;
import lu.kbra.school_lu.db.table.ExerciseTable;
import lu.kbra.school_lu.db.table.UserTable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserExerciseData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = UserTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	private Long userId;

	@Column
	@ForeignKey(table = ExerciseTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	private Long exerciseId;

	@Column
	@DefaultValue("{F:current_timestamp}")
	private Instant timestamp;

	@Column
	@MaxLength(24)
	private ExerciseStatus status;

	public UserExerciseData(final Long id) {
		this.id = id;
	}

	public UserExerciseData(final Long userId, final Long exerciseId, final Instant timestamp, final ExerciseStatus status) {
		this.userId = userId;
		this.exerciseId = exerciseId;
		this.timestamp = timestamp;
		this.status = status;
	}

	@Override
	public UserExerciseData clone() {
		return PCUtils.safeClone(super::clone);
	}
}
