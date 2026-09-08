package lu.kbra.school_lu.db.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;

@Data
@NoArgsConstructor
public class ExamPartData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@Nullable
	@MaxLength(128)
	private String name;

	public ExamPartData(Long id) {
		this.id = id;
	}

	public ExamPartData(String name) {
		this.name = name;
	}

}
