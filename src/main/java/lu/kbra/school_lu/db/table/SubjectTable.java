package lu.kbra.school_lu.db.table;

import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.SubjectData;

@Component
public abstract class SubjectTable extends DeferredDatabaseTable<SubjectData> {

	public SubjectTable(final DeferredDatabase database) {
		super(database);
	}

	@Query
	public abstract List<SubjectData> all();

	@Query(retColumns = { "{M:name}" })
	public abstract List<String> allNames();

	@Query(retColumns = { "{M:name}" }, tables = { @Table(typeName = SectionTable.class) })
	public abstract List<String> allNames(@Param("{M:SectionTable:name}") String section);

}
