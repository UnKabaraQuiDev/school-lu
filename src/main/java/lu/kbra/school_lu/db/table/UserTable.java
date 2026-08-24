package lu.kbra.school_lu.db.table;

import java.util.Optional;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.UserData;

@Component
public abstract class UserTable extends DeferredDatabaseTable<UserData> {

	public UserTable(Database database) {
		super(database);
	}

	@Query
	public abstract Optional<UserData> byUsername(@Param String username);

	public UserData get(long id) {
		return super.load(new UserData(id));
	}

}