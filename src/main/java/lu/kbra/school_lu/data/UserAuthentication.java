package lu.kbra.school_lu.data;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public final class UserAuthentication implements Authentication {

	private static final long serialVersionUID = -3011822382783701952L;
	private static final Collection<? extends GrantedAuthority> EMPTY_SET = Collections.emptySet();

	private final UserId userId;
	private boolean authenticated;

	public UserAuthentication(final UserId userId) {
		this.userId = userId;
		this.authenticated = true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return UserAuthentication.EMPTY_SET;
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
	public void setAuthenticated(final boolean authenticated) {
		this.authenticated = authenticated;
	}

	@Override
	public String getName() {
		return this.userId.id().toString();
	}

}
