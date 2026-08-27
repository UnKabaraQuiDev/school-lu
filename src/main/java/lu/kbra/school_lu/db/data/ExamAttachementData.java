package lu.kbra.school_lu.db.data;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.domain.table.ForeignKeyData.OnAction;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.db.table.ExamTable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExamAttachementData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@Unique
	@ForeignKey(table = ExamTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	private Long examId;

	@Column
	@Unique
	@MaxLength(64)
	private String qualifier;

	@Column
	@Nullable
	@MaxLength(128)
	private String name;

	@Column
	@Unique
	private String location;

	public ExamAttachementData(Long id) {
		this.id = id;
	}

	public ExamAttachementData(Long examId, @MaxLength(64) String qualifier) {
		this.examId = examId;
		this.qualifier = qualifier;
	}

	public ExamAttachementData(Long examId, @MaxLength(64) String qualifier, @MaxLength(128) String name, String location) {
		this.examId = examId;
		this.qualifier = qualifier;
		this.name = name;
		this.location = location;
	}

	@Override
	public ExamAttachementData clone() {
		return PCUtils.safeClone(super::clone);
	}

}
