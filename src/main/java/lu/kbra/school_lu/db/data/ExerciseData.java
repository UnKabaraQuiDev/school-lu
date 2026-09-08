package lu.kbra.school_lu.db.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.domain.table.ForeignKeyData.OnAction;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.db.table.ExamPartTable;

@Data
@NoArgsConstructor
public class ExerciseData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = ExamPartTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	private Long examPartId;

	@Column
	@Unique
	private int exerciseIndex;

	@Column
	@Nullable
	@MaxLength(64)
	private String name;

	public ExerciseData(final Long id) {
		this.id = id;
	}

	public ExerciseData(Long examPartId, int exerciseIndex) {
		this.examPartId = examPartId;
		this.exerciseIndex = exerciseIndex;
	}

	public ExerciseData(Long examPartId, int exerciseIndex, String name) {
		this.examPartId = examPartId;
		this.exerciseIndex = exerciseIndex;
		this.name = name;
	}

}
