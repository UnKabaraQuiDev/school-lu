package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExamData;
import lu.kbra.school_lu.db.data.ExerciseData;

@Component
public abstract class ExamTable extends DeferredDatabaseTable<ExamData> {

	public ExamTable(final DeferredDatabase database) {
		super(database);
	}

	@Query(distinct = true)
	public abstract ExamData byExercise(@Param ExerciseData c);

}
