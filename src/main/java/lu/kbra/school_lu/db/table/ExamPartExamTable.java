package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExamPartExamData;

@Component
public abstract class ExamPartExamTable extends DeferredDatabaseTable<ExamPartExamData> {

	protected ExamPartExamTable(DeferredDatabase database) {
		super(database);
	}

}
