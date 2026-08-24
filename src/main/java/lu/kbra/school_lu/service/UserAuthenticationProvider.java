package lu.kbra.school_lu.service;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lu.kbra.school_lu.data.UserAuthentication;
import lu.kbra.school_lu.data.UserId;
import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.db.table.UserTable;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

	private final UserTable userTable;
	private final PasswordEncoder passwordEncoder;

	public UserAuthenticationProvider(UserTable userTable, PasswordEncoder passwordEncoder) {

		this.userTable = userTable;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		final String username = authentication.getName();
		final String password = String.valueOf(authentication.getCredentials());

		final UserData user = this.userTable.byUsername(username)
				.orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

		if (!user.isEnabled()) {
			throw new BadCredentialsException("User is disabled");
		}

		if (!this.passwordEncoder.matches(password, user.getPasswordHash())) {
			throw new BadCredentialsException("Invalid username or password");
		}

		return new UserAuthentication(new UserId(user.getId()), List.of());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}