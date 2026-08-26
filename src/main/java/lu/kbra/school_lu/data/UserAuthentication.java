package lu.kbra.school_lu.data;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public final class UserAuthentication implements Authentication {

	private static final long serialVersionUID = -3011822382783701952L;

	private final UserId userId;
	private final Collection<? extends GrantedAuthority> authorities;
	private boolean authenticated;

	public UserAuthentication(UserId userId, Collection<? extends GrantedAuthority> authorities) {

		this.userId = userId;
		this.authorities = authorities;
		this.authenticated = true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public UserId getPrincipal() {
		return this.userId;
	}

	@Override
	public boolean isAuthenticated() {
		return this.authenticated;
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	@Override
	public String getName() {
		return this.userId.id().toString();
	}

}
