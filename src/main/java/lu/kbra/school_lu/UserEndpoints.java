package lu.kbra.school_lu;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lu.kbra.school_lu.data.UserId;
import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserEndpoints {

	public record UserResponse(Long id, String username, String email) {

	}

	private final UserService userService;

	@GetMapping("/me")
	public UserResponse me(@AuthenticationPrincipal UserId userId) {
		final UserData user = this.userService.get(userId);

		return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
	}

}
