package lu.kbra.school_lu.service;

import java.time.Instant;
import java.util.Locale;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lu.kbra.school_lu.data.UserAuthentication;
import lu.kbra.school_lu.data.UserId;
import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.db.table.UserTable;
import lu.kbra.school_lu.exceptions.EmailAlreadyExistsException;
import lu.kbra.school_lu.exceptions.UsernameAlreadyExistsException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserTable userTable;
	private final PasswordEncoder passwordEncoder;

	public UserData get(final UserId userId) {
		return this.userTable.byId(userId.id());
	}

	public UserData get(final long id) {
		return this.userTable.byId(id);
	}

	public UserData get(final UserAuthentication auth) {
		return this.userTable.byId(auth.getPrincipal().id());
	}

	public UserData register(String username, String email, final String password)
			throws UsernameAlreadyExistsException,
				EmailAlreadyExistsException {
		username = username.trim();
		email = email.trim().toLowerCase(Locale.ROOT);

		if (this.userTable.existsByUsername(username)) {
			throw new UsernameAlreadyExistsException();
		}

		if (this.userTable.existsByEmail(email)) {
			throw new EmailAlreadyExistsException();
		}

		final String passwordHash = this.passwordEncoder.encode(password);

		final UserData user = new UserData(username, email, passwordHash);
		user.setEnabled(true);

		return this.userTable.insertAndReload(user);
	}

	public void updateLastLogin(final Authentication authentication) {
		final UserAuthentication userAuthentication = (UserAuthentication) authentication;

		final UserId userId = userAuthentication.getPrincipal();
		final UserData user = this.userTable.byId(userId.id());
		user.setLastLogin(Instant.now());

		this.userTable.updateAndReload(user);
	}

}
