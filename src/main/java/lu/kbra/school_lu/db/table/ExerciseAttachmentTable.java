package lu.kbra.school_lu.db.table;

import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExerciseAttachmentData;
import lu.kbra.school_lu.db.data.ExerciseData;

@Component
public abstract class ExerciseAttachmentTable extends DeferredDatabaseTable<ExerciseAttachmentData> {

	public ExerciseAttachmentTable(final DeferredDatabase database) {
		super(database);
	}

	@Query
	public abstract List<ExerciseAttachmentData> byExercise(@Param ExerciseData c);

}
