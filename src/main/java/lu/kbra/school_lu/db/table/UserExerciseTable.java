package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.UserExerciseData;

@Component
public abstract class UserExerciseTable extends DeferredDatabaseTable<UserExerciseData> {

	public UserExerciseTable(final DeferredDatabase database) {
		super(database);
	}

}
