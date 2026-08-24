package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.UserExerciseData;

@Component
public abstract class UserExerciseTable extends DeferredDatabaseTable<UserExerciseData> {

	public UserExerciseTable(Database database) {
		super(database);
	}

}