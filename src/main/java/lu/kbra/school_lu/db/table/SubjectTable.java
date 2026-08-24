package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.SubjectData;

@Component
public abstract class SubjectTable extends DeferredDatabaseTable<SubjectData> {

	public SubjectTable(Database database) {
		super(database);
	}

}