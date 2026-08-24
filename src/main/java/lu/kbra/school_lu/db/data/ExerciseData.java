package lu.kbra.school_lu.db.data;

import lombok.Data;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.db.table.ExamTable;

@Data
public class ExerciseData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = ExamTable.class)
	@Unique(1)
	private Long examId;

	@Column
	@Unique(1)
	private int exerciseIndex;

	@Column
	@MaxLength(1024)
	private String image;

	public ExerciseData(Long id) {
		this.id = id;
	}

	public ExerciseData(Long examId, int exerciseIndex, String image) {
		this.examId = examId;
		this.exerciseIndex = exerciseIndex;
		this.image = image;
	}

	@Override
	public ExerciseData clone() {
		return PCUtils.safeClone(super::clone);
	}
}