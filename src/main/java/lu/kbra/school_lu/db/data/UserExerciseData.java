package lu.kbra.school_lu.db.data;

import java.time.Instant;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.DefaultValue;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.data.ExerciseStatus;
import lu.kbra.school_lu.db.table.ExerciseTable;
import lu.kbra.school_lu.db.table.UserTable;

import lombok.Data;

@Data
public class UserExerciseData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = UserTable.class)
	private Long userId;

	@Column
	@ForeignKey(table = ExerciseTable.class)
	private Long exerciseId;

	@Column
	@DefaultValue("{F:current_timestamp}")
	private Instant timestamp;

	@Column
	@MaxLength(24)
	private ExerciseStatus status;

	public UserExerciseData(Long id) {
		this.id = id;
	}

	public UserExerciseData(Long userId, Long exerciseId, Instant timestamp, ExerciseStatus status) {
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
