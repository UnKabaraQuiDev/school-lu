package lu.kbra.school_lu.db.data;

import java.time.Instant;

import lombok.Data;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.DefaultValue;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.Version;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;

@Data
public class UserData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@Unique(1)
	@MaxLength(100)
	private String username;

	@Column
	@Unique(2)
	@MaxLength(320)
	private String email;

	@Column
	@MaxLength(255)
	private String passwordHash;

	@Column
	@Version(default_ = "{F:current_timestamp}", expr = "{F:current_timestamp}")
	private Instant lastUpdate;

	@Column
	@DefaultValue("{F:current_timestamp}")
	private Instant createdAt;

	@Column
	@DefaultValue("{F:current_timestamp}")
	private Instant lastLogin;

	@Column
	private boolean enabled;

	public UserData(Long id) {
		this.id = id;
	}

	public UserData(String username, String email, String passwordHash) {
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		this.enabled = true;
	}

	@Override
	public UserData clone() {
		return PCUtils.safeClone(super::clone);
	}

}