package lu.kbra.school_lu.data;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lu.kbra.school_lu.db.data.UserData;

public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = -5167192533015592360L;

	private final UserData user;

	public UserDetailsImpl(final UserData user) {
		this.user = user;
	}

	@Override
	public String getUsername() {
		return this.user.getUsername();
	}

	@Override
	public String getPassword() {
		return this.user.getPasswordHash();
	}

	@Override
	public boolean isEnabled() {
		return this.user.isEnabled();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
}
