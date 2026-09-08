package lu.kbra.school_lu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.stereotype.Component;

import lu.kbra.school_lu.data.UserAuthentication;
import lu.kbra.school_lu.data.UserDetailsImpl;
import lu.kbra.school_lu.data.UserId;
import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.db.table.UserTable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserRememberMeServices extends AbstractRememberMeServices {

	private final UserTable userTable;

	public UserRememberMeServices(
			@Value("${app.security.remember-me.sk}") final String key,
			final UserDetailsService userDetailsService,
			final UserTable userTable) {
		super(key, userDetailsService);
		this.userTable = userTable;
		this.setParameter("remember-me");
	}

	@Override
	protected UserDetails
			processAutoLoginCookie(final String[] cookieTokens, final HttpServletRequest request, final HttpServletResponse response) {
		if (cookieTokens.length != 3) {
			throw new InvalidCookieException("Invalid remember-me cookie");
		}

		final String username = cookieTokens[0];

		final UserData user = this.userTable.byUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

		if (!user.isEnabled()) {
			throw new UsernameNotFoundException(username);
		}

		return new UserDetailsImpl(user);
	}

	@Override
	protected Authentication createSuccessfulAuthentication(final HttpServletRequest request, final UserDetails user) {
		final UserData userData = (UserData) user;

		return new UserAuthentication(new UserId(userData.getId()));
	}

	@Override
	protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
		System.err.println("logged in");
	}

}
