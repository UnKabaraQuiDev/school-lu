package lu.kbra.school_lu.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lu.kbra.school_lu.data.UserId;
import lu.kbra.school_lu.service.UserAuthenticationProvider;
import lu.kbra.school_lu.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(
			final HttpSecurity http,
			@Qualifier("corsConfigurationSource") final CorsConfigurationSource source,
			final UserAuthenticationProvider authenticationProvider,
			final UserService userService)
			throws Exception {
		return http.cors(cors -> cors.configurationSource(source))
				.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
						.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
				.authenticationProvider(authenticationProvider)
				.authorizeHttpRequests(auth -> auth.requestMatchers("/login", "/register", "/logout", "/error", "/csrf")
						.permitAll()
						.anyRequest()
						.authenticated())
				.formLogin(form -> form.loginPage("/login")
						.failureHandler((request, response, exception) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
						.successHandler((request, response, authentication) -> {
							userService.updateLastLogin(authentication);
							response.setStatus(HttpServletResponse.SC_OK);
							SecurityConfig.log.info("User: {} logged in from {} [{}]",
									((UserId) authentication.getPrincipal()).id(),
									request.getRemoteAddr(),
									request.getRemoteHost());
						})
						.permitAll())
				.logout(logout -> logout.logoutSuccessHandler((request, response, authentication) -> {
					if (authentication != null) {
						SecurityConfig.log.info("User: {} logged out from {} [{}]",
								((UserId) authentication.getPrincipal()).id(),
								request.getRemoteAddr(),
								request.getRemoteHost());
					}

					response.setStatus(HttpServletResponse.SC_OK);
				}).clearAuthentication(true))
				.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.urls}") final List<String> hosts) {
		SecurityConfig.log.info("Allowing origins: {}", hosts);

		final CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(hosts);
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN"));
		configuration.setAllowCredentials(true);

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityContextRepository securityContextRepository() {
		return new HttpSessionSecurityContextRepository();
	}

	@Bean
	AuthenticationManager authenticationManager(final AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

}
