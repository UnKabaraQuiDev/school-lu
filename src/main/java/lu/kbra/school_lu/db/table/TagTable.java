package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.TagData;

@Component
public abstract class TagTable extends DeferredDatabaseTable<TagData> {

	public TagTable(Database database) {
		super(database);
	}

}