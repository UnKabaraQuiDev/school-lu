package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExamAttachmentData;

@Component
public abstract class ExamAttachmentTable extends DeferredDatabaseTable<ExamAttachmentData> {

	public ExamAttachmentTable(final DeferredDatabase database) {
		super(database);
	}

}
