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
import lu.kbra.school_lu.data.UserId;
import lu.kbra.school_lu.data.UserPermissionType;
import lu.kbra.school_lu.db.data.UserPermissionData;
import lu.kbra.school_lu.db.table.UserPermissionTable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPermissionService {

	private final UserPermissionTable userPermissionTable;

	public EnumSet<UserPermissionType> getPermissions(final UserId id) {
		return this.userPermissionTable.byUserId(id.id())
				.stream()
				.map(UserPermissionData::getPermission)
				.collect(Collectors.toCollection(() -> EnumSet.noneOf(UserPermissionType.class)));
	}

	public void setPermissions(final UserId id, final Set<UserPermissionType> set) {
		try (DeferredDBTransaction transaction = this.userPermissionTable.getDatabase().createTransaction()) {
			final UserPermissionTable userPermissionProxy = transaction.use(this.userPermissionTable);

			final List<UserPermissionData> datas = userPermissionProxy.byUserId(id.id());
			final List<UserPermissionData> toKeep = new ArrayList<>();
			datas.removeIf(c -> {
				if (set.contains(c.getPermission())) {
					toKeep.add(c);
					return true;
				}

				return false;
			});
			userPermissionProxy.deleteAll(datas);
			userPermissionProxy.updateAll(toKeep);

			transaction.commit();
		}
	}

	public void requireAnyPermission(UserId userId, UserPermissionType... manageExam) {
		System.err.println("test");
		final Set<UserPermissionType> perms = this.getPermissions(userId);
		if (!Arrays.stream(manageExam).anyMatch(perms::contains)) {
			log.info("Permission refused for user: " + userId + ", required any of: " + Arrays.toString(manageExam) + ", got: " + perms);
			throw new AccessDeniedException("Permission refused, required any of: " + Arrays.toString(manageExam) + ", got: " + perms);
		}
	}

	public void requireAllPermissions(UserId userId, UserPermissionType... manageExam) {
		final Set<UserPermissionType> perms = this.getPermissions(userId);
		if (!Arrays.stream(manageExam).allMatch(perms::contains)) {
			log.info("Permission refused for user: " + userId + ", required all of: " + Arrays.toString(manageExam) + ", got: " + perms);
			throw new AccessDeniedException("Permission refused, required all of: " + Arrays.toString(manageExam) + ", got: " + perms);
		}
	}

}
