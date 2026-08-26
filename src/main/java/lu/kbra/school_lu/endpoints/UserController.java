package lu.kbra.school_lu.endpoints;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lu.kbra.school_lu.data.UserId;
import lu.kbra.school_lu.data.UserPermissionType;
import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.service.UserConfigService;
import lu.kbra.school_lu.service.UserPermissionService;
import lu.kbra.school_lu.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	public record UserInfo(
			String name,
			String email,
			Instant createdAt,
			Map<String, String> config,
			EnumSet<UserPermissionType> permissions) {

	}

	private final UserService userService;
	private final UserConfigService userConfigService;
	private final UserPermissionService userPermissionService;

	@GetMapping("/me")
	public UserInfo me(@AuthenticationPrincipal final UserId userId) {
		final UserData userData = this.userService.get(userId);

		return new UserInfo(userData.getUsername(),
				userData.getEmail(),
				userData.getCreatedAt(),
				this.userConfigService.getConfig(userId),
				this.userPermissionService.getPermissions(userId));
	}

}
