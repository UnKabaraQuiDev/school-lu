package lu.kbra.school_lu.db.table;

import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.UserPermissionData;

@Component
public abstract class UserPermissionTable extends DeferredDatabaseTable<UserPermissionData> {

	public UserPermissionTable(final DeferredDatabase database) {
		super(database);
	}

	@Query
	public abstract List<UserPermissionData> byUserId(@Param final long userId);

}
