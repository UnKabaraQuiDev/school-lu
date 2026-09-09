package lu.kbra.school_lu.db.data;

import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.db.table.ExamPartTable;
import lu.kbra.school_lu.db.table.ExamTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamPartExamData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@ForeignKey(table = ExamPartTable.class)
	private Long examPartId;

	@Column
	@PrimaryKey
	@ForeignKey(table = ExamTable.class)
	private Long examId;

}
