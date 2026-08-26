package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.UserConnectionData;

@Component
public abstract class UserConnectionTable extends DeferredDatabaseTable<UserConnectionData> {

	public UserConnectionTable(final DeferredDatabase database) {
		super(database);
	}

}
