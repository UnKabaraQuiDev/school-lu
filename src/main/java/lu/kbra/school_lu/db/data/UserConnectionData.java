package lu.kbra.school_lu.db.data;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.domain.table.ForeignKeyData.OnAction;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.db.table.UserTable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserConnectionData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = UserTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
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

	public UserConnectionData(final Long id) {
		this.id = id;
	}

	public UserConnectionData(final Long userId, final String provider, final String providerUserId, final String email) {
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
