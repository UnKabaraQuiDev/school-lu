package lu.kbra.school_lu.db.table;

import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.query.Query.Type;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.UserConfigData;
import lu.kbra.school_lu.db.data.UserData;

@Component
public abstract class UserConfigTable extends DeferredDatabaseTable<UserConfigData> {

	public UserConfigTable(final DeferredDatabase database) {
		super(database);
	}

	@Query
	public abstract List<UserConfigData> byUserId(@Param long userId);

	@Query(strategy = Type.SINGLE_NULL)
	public abstract UserConfigData byUserIdAndKey(@Param long userId, @Param String key);

	@Query
	public abstract List<UserConfigData> byUser(@Param UserData userId);

	@Query(strategy = Type.SINGLE_NULL)
	public abstract UserConfigData byUserAndKey(@Param UserData userId, @Param String key);

}
