package lu.kbra.school_lu.db.table;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.query.Any;
import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.SelectColumn;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.impl.DatabaseEntry.ReadOnlyDatabaseEntry;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExerciseData;
import lu.kbra.school_lu.db.data.TagData;

@Component
public abstract class TagTable extends DeferredDatabaseTable<TagData> {

	public TagTable(final DeferredDatabase database) {
		super(database);
	}

	@Query
	public abstract List<TagData> all();

	@Query(retColumns = "{M:name}")
	public abstract List<String> byName(@Param Set<String> name);

	@Query(tables = { @Table(typeName = ExerciseTagTable.class), @Table(typeName = ExerciseTable.class) })
	public abstract List<TagData> byExercise(@Param ExerciseData c);

	@Query(
			retColumns = { "{M:TagTable:id}", "{M:TagTable:name}" },
			tables = { @Table(columns = { @SelectColumn(func = "count(*)", asName = "count") }, typeName = ExerciseTagTable.class) },
			groupBy = { "{M:TagTable:id}" }
	)
	public abstract List<TagProp> propByName(@Param @Any Set<String> name);

	public record TagProp(@Column int id, @Column String name, @Column int count) implements ReadOnlyDatabaseEntry {
	}

}
