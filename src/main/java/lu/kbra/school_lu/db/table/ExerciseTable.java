package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExerciseData;

@Component
public abstract class ExerciseTable extends DeferredDatabaseTable<ExerciseData> {

	public ExerciseTable(Database database) {
		super(database);
	}

}