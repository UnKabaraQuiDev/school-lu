package lu.kbra.school_lu.db.data;

import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.domain.table.ForeignKeyData.OnAction;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.data.ExamAttachmentType;
import lu.kbra.school_lu.db.table.ExamPartTable;

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
	@ForeignKey(table = ExamPartTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	private Long examPartId;

	@Column
	@Unique(1)
	@MaxLength(16)
	private ExamAttachmentType qualifier;

	@Column
	@Unique(2)
	private String location;

	public ExamAttachmentData(Long id) {
		this.id = id;
	}

	public ExamAttachmentData(String location) {
		this.location = location;
	}

	public ExamAttachmentData(Long examPartId, ExamAttachmentType qualifier) {
		this.examPartId = examPartId;
		this.qualifier = qualifier;
	}

	public ExamAttachmentData(Long examPartId, ExamAttachmentType qualifier, String location) {
		this.examPartId = examPartId;
		this.qualifier = qualifier;
		this.location = location;
	}

}
