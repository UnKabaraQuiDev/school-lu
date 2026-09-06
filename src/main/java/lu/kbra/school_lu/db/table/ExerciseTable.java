package lu.kbra.school_lu.db.table;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.db.data.ExerciseData;
import lu.kbra.school_lu.db.data.SubjectData;
import lu.kbra.school_lu.db.data.TagData;

@Component
public abstract class ExerciseTable extends DeferredDatabaseTable<ExerciseData> {

	private static final String hasSolutionAndStatement = "EXISTS (SELECT 1 FROM {T:ExerciseAttachmentTable} "
			+ "WHERE {M:ExerciseAttachmentTable:exerciseId} = {M:ExerciseTable:id} AND {M:ExerciseAttachmentTable:qualifier} = 'SOLUTION') "
			+ "AND EXISTS (SELECT 1 FROM {T:ExerciseAttachmentTable} "
			+ "WHERE {M:ExerciseAttachmentTable:exerciseId} = {M:ExerciseTable:id} AND {M:ExerciseAttachmentTable:qualifier} = 'STATEMENT')";

	public ExerciseTable(final DeferredDatabase database) {
		super(database);
	}

	@Query(distinct = true, condition = hasSolutionAndStatement)
	public abstract List<ExerciseData> allWithSolution();

	@Query(
			distinct = true,
			tables = { @Table(typeName = ExamTable.class), @Table(typeName = SubjectTable.class) },
			condition = hasSolutionAndStatement
	)
	public abstract List<ExerciseData> withSolutionAnySubject(@Param Collection<SubjectData> subjects);

	@Query(
			distinct = true,
			tables = {
					@Table(typeName = ExamTable.class),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class),
					@Table(typeName = TagTable.class) },
			condition = hasSolutionAndStatement
	)
	public abstract List<ExerciseData>
			withSolutionAnySubjectAnyTag(@Param Collection<SubjectData> subjects, @Param Collection<TagData> tagData);

	@Query(
			distinct = true,
			tables = {
					@Table(typeName = ExamTable.class),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class),
					@Table(typeName = TagTable.class) },
			condition = hasSolutionAndStatement
	)
	public abstract List<ExerciseData> withSolutionAnySubjectAllTags(@Param Collection<SubjectData> subjects, Collection<TagData> tagData);

}
