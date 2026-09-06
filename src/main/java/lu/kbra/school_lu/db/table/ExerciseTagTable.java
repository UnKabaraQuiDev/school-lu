package lu.kbra.school_lu.db.table;

import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExerciseData;
import lu.kbra.school_lu.db.data.ExerciseTagData;

@Component
public abstract class ExerciseTagTable extends DeferredDatabaseTable<ExerciseTagData> {

	public ExerciseTagTable(final DeferredDatabase database) {
		super(database);
	}

	@Query
	public abstract List<ExerciseTagData> byExercise(@Param ExerciseData exerciseId);

}
