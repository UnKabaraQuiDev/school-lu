package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.query.Query.Type;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.data.ExamAttachmentType;
import lu.kbra.school_lu.db.data.ExamAttachmentData;
import lu.kbra.school_lu.db.data.ExamData;

@Component
public abstract class ExamAttachmentTable extends DeferredDatabaseTable<ExamAttachmentData> {

	public ExamAttachmentTable(final DeferredDatabase database) {
		super(database);
	}

	@Query(
			distinct = true,
			tables = {
					@Table(typeName = ExamPartTable.class),
					@Table(typeName = ExamPartExamTable.class),
					@Table(typeName = ExamTable.class) },
			strategy = Type.SINGLE_THROW
	)
	public abstract ExamAttachmentData byExamAndPartNameAndQualifier(
			@Param ExamData exam,
			@Param("{M:ExamPartTable:name}") String partName,
			@Param ExamAttachmentType qualifier);

}
