package lu.kbra.school_lu.db.data;

import java.time.Instant;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.DefaultValue;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.Version;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.school_lu.data.UserId;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@Unique(1)
	@NotBlank
	@MaxLength(100)
	private String username;

	@Column
	@Unique(2)
	@NotBlank
	@Email
	@MaxLength(320)
	private String email;

	@Column
	@NotBlank
	@MaxLength(255)
	private String passwordHash;

	@Column
	@Version(default_ = "{F:current_timestamp}", expr = "{F:current_timestamp}")
	private Instant lastUpdate;

	@Column
	@DefaultValue("{F:current_timestamp}")
	private Instant createdAt;

	@Column
	@Nullable
	private Instant lastLogin;

	@Column
	private boolean enabled;

	public UserData(final Long id) {
		this.id = id;
	}

	public UserData(String email) {
		this.email = email;
	}

	public UserData(final String username, final String email, final String passwordHash) {
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		this.enabled = true;
	}

	@Override
	public UserData clone() {
		return PCUtils.safeClone(super::clone);
	}

	public UserId toUserId() {
		return new UserId(this.id);
	}

}
