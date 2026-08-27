package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExamAttachementData;

@Component
public abstract class ExamAttachementTable extends DeferredDatabaseTable<ExamAttachementData> {

	public ExamAttachementTable(final DeferredDatabase database) {
		super(database);
	}

}
