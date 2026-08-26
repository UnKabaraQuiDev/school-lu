package lu.kbra.school_lu.db.data;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.db.table.SubjectTable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExamData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = SubjectTable.class)
	@Unique(1)
	private Long subjectId;

	@Column
	@Unique(1)
	private int year;

	@Column
	@Unique(1)
	private int season;

	@Column
	@Unique(1)
	private boolean retry;

	public ExamData(final Long id) {
		this.id = id;
	}

	public ExamData(final Long subjectId, final int year, final int season, final boolean retry) {
		this.subjectId = subjectId;
		this.year = year;
		this.season = season;
		this.retry = retry;
	}

	@Override
	public ExamData clone() {
		return PCUtils.safeClone(super::clone);
	}
}
