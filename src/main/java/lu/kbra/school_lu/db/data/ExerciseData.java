package lu.kbra.school_lu.db.data;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.domain.table.ForeignKeyData.OnAction;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.db.table.ExamTable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExerciseData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = ExamTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	@Unique(1)
	private Long examId;

	@Column
	@Unique(1)
	private int exerciseIndex;

	public ExerciseData(final Long id) {
		this.id = id;
	}

	public ExerciseData(final Long examId, final int exerciseIndex) {
		this.examId = examId;
		this.exerciseIndex = exerciseIndex;
	}

	@Override
	public ExerciseData clone() {
		return PCUtils.safeClone(super::clone);
	}
}
