package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExerciseTagData;

@Component
public abstract class ExerciseTagTable extends DeferredDatabaseTable<ExerciseTagData> {

	public ExerciseTagTable(DeferredDatabase database) {
		super(database);
	}

}
