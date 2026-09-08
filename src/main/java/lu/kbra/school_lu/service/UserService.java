package lu.kbra.school_lu.service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.db.table.UserTable;
import lu.kbra.school_lu.exceptions.EmailAlreadyExistsException;
import lu.kbra.school_lu.exceptions.UsernameAlreadyExistsException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserTable userTable;
	private final PasswordEncoder passwordEncoder;

	public UserData get(final String name) {
		return this.userTable.byUsername(name).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	public Optional<UserData> optGet(final String name) {
		return this.userTable.byUsername(name);
	}

	public UserData get(final UserDetails auth) {
		return this.userTable.byUsername(auth.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	public Optional<UserData> optGet(final UserDetails auth) {
		return this.userTable.byUsername(auth.getUsername());
	}

	public UserData get(final Authentication auth) {
		return this.userTable.byUsername(auth.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	public Optional<UserData> optGet(final Authentication auth) {
		return this.userTable.byUsername(auth.getName());
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
		final String username = authentication.getName();

		final UserData user = this.userTable.byUsername(username).orElseThrow();

		user.setLastLogin(Instant.now());
		this.userTable.updateAndReload(user);
	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		final UserData userData = this.userTable.byUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
		return new User(userData.getUsername(), userData.getPasswordHash(), userData.isEnabled(), true, true, true, List.of());
	}

}
