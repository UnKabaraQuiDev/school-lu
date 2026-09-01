package lu.kbra.school_lu.db.data;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.domain.table.ForeignKeyData.OnAction;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.data.ExamSeason;
import lu.kbra.school_lu.data.ExamType;
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
	@ForeignKey(table = SubjectTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	@Unique(1)
	private Long subjectId;

	@Column
	@Unique(1)
	private int year;

	@Column
	@Unique(1)
	private ExamSeason season;

	@Column
	@Unique(1)
	private ExamType retry;

	public ExamData(final Long id) {
		this.id = id;
	}

	public ExamData(Long subjectId, int year, ExamSeason season, ExamType retry) {
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
