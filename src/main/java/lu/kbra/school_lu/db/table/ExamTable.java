package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExamData;

@Component
public abstract class ExamTable extends DeferredDatabaseTable<ExamData> {

	public ExamTable(Database database) {
		super(database);
	}

}