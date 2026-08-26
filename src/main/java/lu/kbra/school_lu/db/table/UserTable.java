package lu.kbra.school_lu.db.table;

import java.util.Optional;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.UserData;

@Component
public abstract class UserTable extends DeferredDatabaseTable<UserData> {

	public UserTable(final DeferredDatabase database) {
		super(database);
	}

	@Query
	public abstract Optional<UserData> byUsername(@Param String username);

	@Query
	public abstract Optional<UserData> byEmail(@Param String email);

	public UserData byId(final long id) {
		return super.load(new UserData(id));
	}

	public boolean existsByUsername(final String username) {
		return this.byUsername(username).isPresent();
	}

	public boolean existsByEmail(final String email) {
		return this.byEmail(email).isPresent();
	}

}
