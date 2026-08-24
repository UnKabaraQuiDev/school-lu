package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.SectionData;

@Component
public abstract class SectionTable extends DeferredDatabaseTable<SectionData> {

	public SectionTable(Database database) {
		super(database);
	}

}