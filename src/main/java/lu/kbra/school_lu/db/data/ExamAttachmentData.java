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
public class ExamAttachmentData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@Unique(1)
	@ForeignKey(table = ExamTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	private Long examId;

	@Column
	@Unique(1)
	@MaxLength(64)
	private String qualifier;

	@Column
	@Nullable
	@MaxLength(128)
	private String name;

	@Column
	@Unique(2)
	private String location;

	public ExamAttachmentData(Long id) {
		this.id = id;
	}

	public ExamAttachmentData(String location) {
		this.location = location;
	}

	public ExamAttachmentData(Long examId, String qualifier) {
		this.examId = examId;
		this.qualifier = qualifier;
	}

	public ExamAttachmentData(Long examId, String qualifier, String name, String location) {
		this.examId = examId;
		this.qualifier = qualifier;
		this.name = name;
		this.location = location;
	}

	@Override
	public ExamAttachmentData clone() {
		return PCUtils.safeClone(super::clone);
	}

}
