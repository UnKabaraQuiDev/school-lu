package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExerciseData;

@Component
public abstract class ExerciseTable extends DeferredDatabaseTable<ExerciseData> {

	public ExerciseTable(final DeferredDatabase database) {
		super(database);
	}

}
