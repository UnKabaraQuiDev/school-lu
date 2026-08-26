package lu.kbra.school_lu.endpoints;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.exceptions.EmailAlreadyExistsException;
import lu.kbra.school_lu.exceptions.UsernameAlreadyExistsException;
import lu.kbra.school_lu.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	public record RegisterRequest(
			@NotBlank @Size(max = 100) @Pattern(
					regexp = "^[a-zA-Z0-9_.-]+$",
					message = "Username may only contain letters, numbers, '.', '_' and '-'."
			) String username,

			@NotBlank @Email @Size(max = 320) String email,

			@NotBlank @Size(min = 5, max = 72) String password,

			@NotBlank String confirmPassword) {
	}

	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	private final SecurityContextRepository securityContextRepository;

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody final RegisterRequest request, final HttpServletRequest httpRequest) {
		if (!request.password().equals(request.confirmPassword())) {
			return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match."));
		}

		try {
			final UserData userData = this.userService.register(request.username(), request.email(), request.password());

			AuthController.log
					.info("User: {} registered from {} [{}]", userData.getId(), httpRequest.getRemoteAddr(), httpRequest.getRemoteHost());

			final Authentication authentication = this.authenticationManager
					.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));
			final SecurityContext context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(authentication);
			SecurityContextHolder.setContext(context);

			this.securityContextRepository.saveContext(context, httpRequest, null);

			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (final UsernameAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Username is already in use."));
		} catch (final EmailAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email is already in use."));
		}
	}

}
