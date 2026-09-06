package lu.kbra.school_lu.db.table;

import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.SectionData;

@Component
public abstract class SectionTable extends DeferredDatabaseTable<SectionData> {

	public SectionTable(final DeferredDatabase database) {
		super(database);
	}

	@Query
	public abstract List<SectionData> all();

	@Query(retColumns = { "{M:name}" })
	public abstract List<String> allNames();

}
