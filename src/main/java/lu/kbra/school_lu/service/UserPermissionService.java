package lu.kbra.school_lu.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import lu.kbra.pclib.db.impl.DeferredDBTransaction;
import lu.kbra.school_lu.data.UserPermissionType;
import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.db.data.UserPermissionData;
import lu.kbra.school_lu.db.table.UserPermissionTable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPermissionService {

	private final UserPermissionTable userPermissionTable;
	private final UserService userService;

	public EnumSet<UserPermissionType> getPermissions(final UserData userData) {
		return this.userPermissionTable.permissionsByUser(userData)
				.stream()
				.collect(Collectors.toCollection(() -> EnumSet.noneOf(UserPermissionType.class)));
	}

	public void setPermissions(final UserData userData, final Set<UserPermissionType> set) {
		try (DeferredDBTransaction transaction = this.userPermissionTable.getDatabase().createTransaction()) {
			final UserPermissionTable userPermissionProxy = transaction.use(this.userPermissionTable);

			final List<UserPermissionData> datas = userPermissionProxy.byUser(userData);
			final List<UserPermissionData> toKeep = new ArrayList<>();

			datas.removeIf(permission -> {
				if (set.contains(permission.getPermission())) {
					toKeep.add(permission);
					return true;
				}

				return false;
			});

			userPermissionProxy.deleteAll(datas);
			userPermissionProxy.updateAll(toKeep);

			transaction.commit();
		}
	}

	public void requireAnyPermission(final UserData userData, final UserPermissionType... permissions) {
		final Set<UserPermissionType> perms = this.getPermissions(userData);

		if (!Arrays.stream(permissions).anyMatch(perms::contains)) {
			UserPermissionService.log.info("Permission refused for user: {}, required any of: {}, got: {}",
					userData.getUsername(),
					Arrays.toString(permissions),
					perms);

			throw new AccessDeniedException("Permission refused, required any of: " + Arrays.toString(permissions) + ", got: " + perms);
		}
	}

	public void requireAllPermissions(final UserData userData, final UserPermissionType... permissions) {
		final Set<UserPermissionType> perms = this.getPermissions(userData);

		if (!Arrays.stream(permissions).allMatch(perms::contains)) {
			UserPermissionService.log.info("Permission refused for user: {}, required all of: {}, got: {}",
					userData.getUsername(),
					Arrays.toString(permissions),
					perms);

			throw new AccessDeniedException("Permission refused, required all of: " + Arrays.toString(permissions) + ", got: " + perms);
		}
	}

}
