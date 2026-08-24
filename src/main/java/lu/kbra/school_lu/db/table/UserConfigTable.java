package lu.kbra.school_lu.db.table;

import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.UserConfigData;

@Component
public abstract class UserConfigTable extends DeferredDatabaseTable<UserConfigData> {

	public UserConfigTable(Database database) {
		super(database);
	}
	
	@Query
	public abstract List<UserConfigData> byUserId(@Param long userId);

}