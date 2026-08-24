package lu.kbra.school_lu.service;

import org.springframework.stereotype.Service;

import lu.kbra.school_lu.data.UserAuthentication;
import lu.kbra.school_lu.data.UserId;
import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.db.table.UserTable;

@Service
public class UserService {

	private final UserTable userTable;

	public UserService(UserTable userTable) {
		this.userTable = userTable;
	}

	public UserData get(UserId userId) {
		return this.userTable.get(userId.id());
	}

	public UserData get(long id) {
		return this.userTable.get(id);
	}

	public UserData get(UserAuthentication auth) {
		return this.userTable.get(auth.getPrincipal().id());
	}

}