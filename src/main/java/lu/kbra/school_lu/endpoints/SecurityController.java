package lu.kbra.school_lu.endpoints;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {

	@GetMapping("/csrf")
	CsrfToken csrf(final CsrfToken token) {
		token.getToken();
		return token;
	}

}
