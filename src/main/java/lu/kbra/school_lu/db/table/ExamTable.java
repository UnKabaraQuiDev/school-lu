package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExamData;

@Component
public abstract class ExamTable extends DeferredDatabaseTable<ExamData> {

	public ExamTable(DeferredDatabase database) {
		super(database);
	}

}
