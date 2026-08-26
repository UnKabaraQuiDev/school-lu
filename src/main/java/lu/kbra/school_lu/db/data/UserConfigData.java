package lu.kbra.school_lu.db.data;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.db.table.UserTable;

import lombok.Data;

@Data
public class UserConfigData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@ForeignKey(table = UserTable.class)
	private Long userId;

	@Column
	@PrimaryKey
	@MaxLength(100)
	private String key;

	@Column
	private String value;

	public UserConfigData(Long userId, String key) {
		this.userId = userId;
		this.key = key;
	}

	public UserConfigData(Long userId, String key, String value) {
		this.userId = userId;
		this.key = key;
		this.value = value;
	}

	@Override
	public UserConfigData clone() {
		return PCUtils.safeClone(super::clone);
	}

}
