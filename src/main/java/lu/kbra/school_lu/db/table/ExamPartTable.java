package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.query.Query.Type;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExamAttachmentData;
import lu.kbra.school_lu.db.data.ExamData;
import lu.kbra.school_lu.db.data.ExamPartData;
import lu.kbra.school_lu.db.data.ExerciseData;

@Component
public abstract class ExamPartTable extends DeferredDatabaseTable<ExamPartData> {

	public ExamPartTable(final DeferredDatabase database) {
		super(database);
	}

	@Query(distinct = true, retColumns = { "{M:ExamPartTable:name}" }, strategy = Query.Type.SINGLE_NULL)
	public abstract String nameByExercise(@Param ExerciseData c);

	@Query(distinct = true, tables = { @Table(typeName = ExamPartExamTable.class), @Table(typeName = ExamTable.class) }, strategy = Type.SINGLE_THROW)
	public abstract ExamPartData byAttachmentAndExam(@Param ExamAttachmentData attachment, @Param ExamData exam);

}
