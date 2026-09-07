package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExamAttachmentData;
import lu.kbra.school_lu.db.data.ExerciseData;

@Component
public abstract class ExamAttachmentTable extends DeferredDatabaseTable<ExamAttachmentData> {

	public ExamAttachmentTable(final DeferredDatabase database) {
		super(database);
	}

	@Query(
			distinct = true,
			retColumns = { "{M:ExamAttachmentTable:name}" },
			tables = { @Table(typeName = ExerciseAttachmentTable.class), @Table(typeName = ExerciseTable.class) },
			strategy = Query.Type.SINGLE_NULL
	)
	public abstract String nameByExerciseAttachment(@Param ExerciseData c);

}
