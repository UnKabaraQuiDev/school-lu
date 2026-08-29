package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExerciseAttachmentData;

@Component
public abstract class ExerciseAttachmentTable extends DeferredDatabaseTable<ExerciseAttachmentData> {

	public ExerciseAttachmentTable(final DeferredDatabase database) {
		super(database);
	}

}
