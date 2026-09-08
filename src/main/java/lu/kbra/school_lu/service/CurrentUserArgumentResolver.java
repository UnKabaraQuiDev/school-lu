package lu.kbra.school_lu.service;

import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lu.kbra.school_lu.data.CurrentUser;
import lu.kbra.school_lu.db.data.UserData;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

	private final UserService userService;

	@Override
	public boolean supportsParameter(final MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CurrentUser.class) && UserData.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(
			final MethodParameter parameter,
			final ModelAndViewContainer mavContainer,
			final NativeWebRequest webRequest,
			final WebDataBinderFactory binderFactory) {

		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AccessDeniedException("User is not authenticated");
		}

		return this.userService.get(authentication);
	}
}
