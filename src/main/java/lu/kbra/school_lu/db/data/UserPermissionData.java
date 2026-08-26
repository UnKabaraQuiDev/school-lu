package lu.kbra.school_lu.db.data;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.data.UserPermissionType;
import lu.kbra.school_lu.db.table.UserTable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserPermissionData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@ForeignKey(table = UserTable.class)
	private Long userId;

	@Column
	@PrimaryKey
	@MaxLength(32)
	private UserPermissionType permission;

	@Override
	public UserPermissionData clone() {
		return PCUtils.safeClone(super::clone);
	}

}
