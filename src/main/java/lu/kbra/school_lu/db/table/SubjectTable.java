package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.SubjectData;

@Component
public abstract class SubjectTable extends DeferredDatabaseTable<SubjectData> {

	public SubjectTable(final DeferredDatabase database) {
		super(database);
	}

}
