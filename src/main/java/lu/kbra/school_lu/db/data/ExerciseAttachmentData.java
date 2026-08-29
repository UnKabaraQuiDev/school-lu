package lu.kbra.school_lu.db.data;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.DefaultValue;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.domain.table.ForeignKeyData.OnAction;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.db.table.ExamAttachmentTable;
import lu.kbra.school_lu.db.table.ExerciseTable;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExerciseAttachmentData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@Unique(1)
	@ForeignKey(table = ExerciseTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	private Long exerciseId;

	@Column
	@Unique(1)
	@MaxLength(64)
	private String qualifier;

	@Column
	@Unique(1)
	@PositiveOrZero
	@DefaultValue("0")
	private Integer alternativeIndex;

	@Column
	@Unique(2)
	private String location;

	@Column
	@ForeignKey(table = ExamAttachmentTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	private Long examAttachmentId;

	@Column
	@Nullable
	private Rectangle2D.Float additiveRectangle;

	@Column
	@Nullable
	private Rectangle2D.Float[] subtractiveRectangles;

	public ExerciseAttachmentData(Long id) {
		this.id = id;
	}

	public ExerciseAttachmentData(String location) {
		this.location = location;
	}

	public ExerciseAttachmentData(Long exerciseId, String qualifier, Integer alternativeIndex) {
		this.exerciseId = exerciseId;
		this.qualifier = qualifier;
		this.alternativeIndex = alternativeIndex;
	}

	public ExerciseAttachmentData(Long exerciseId, String qualifier, Integer alternativeIndex, String location) {
		this.exerciseId = exerciseId;
		this.qualifier = qualifier;
		this.alternativeIndex = alternativeIndex;
		this.location = location;
	}

	public ExerciseAttachmentData(
			Long exerciseId,
			String qualifier,
			Integer alternativeIndex,
			String location,
			Long examAttachmentId,
			Float additiveRectangle,
			Float[] subtractiveRectangles) {
		this.exerciseId = exerciseId;
		this.qualifier = qualifier;
		this.alternativeIndex = alternativeIndex;
		this.location = location;
		this.examAttachmentId = examAttachmentId;
		this.additiveRectangle = additiveRectangle;
		this.subtractiveRectangles = subtractiveRectangles;
	}

	@Override
	public ExerciseAttachmentData clone() {
		return PCUtils.safeClone(super::clone);
	}

}
