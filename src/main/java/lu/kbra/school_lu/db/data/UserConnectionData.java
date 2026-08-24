package lu.kbra.school_lu.db.data;


import lombok.Data;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.db.table.UserTable;

@Data
public class UserConnectionData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = UserTable.class)
	private Long userId;

	@Column
	@Unique(1)
	@MaxLength(50)
	private String provider;

	@Column
	@Unique(1)
	@MaxLength(255)
	private String providerUserId;

	@Column
	@MaxLength(320)
	private String email;

	public UserConnectionData(Long id) {
		this.id = id;
	}

	public UserConnectionData(Long userId, String provider, String providerUserId, String email) {
		this.userId = userId;
		this.provider = provider;
		this.providerUserId = providerUserId;
		this.email = email;
	}

	@Override
	public UserConnectionData clone() {
		return PCUtils.safeClone(super::clone);
	}
}