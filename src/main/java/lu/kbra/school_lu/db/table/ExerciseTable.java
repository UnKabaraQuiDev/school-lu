package lu.kbra.school_lu.db.table;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.All;
import lu.kbra.pclib.db.annotations.query.Limit;
import lu.kbra.pclib.db.annotations.query.OrIsNull;
import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.SelectColumn;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.data.ExerciseStatus;
import lu.kbra.school_lu.db.data.ExerciseData;
import lu.kbra.school_lu.db.data.SubjectData;
import lu.kbra.school_lu.db.data.UserData;

@Component
public abstract class ExerciseTable extends DeferredDatabaseTable<ExerciseData> {

	private static final String hasSolutionAndStatement = """
			EXISTS (SELECT 1 FROM {T:ExerciseAttachmentTable} \
			WHERE {M:ExerciseAttachmentTable:exerciseId} = {M:ExerciseTable:id} AND {M:ExerciseAttachmentTable:qualifier} = 'SOLUTION') \
			AND EXISTS (SELECT 1 FROM {T:ExerciseAttachmentTable} \
			WHERE {M:ExerciseAttachmentTable:exerciseId} = {M:ExerciseTable:id} AND {M:ExerciseAttachmentTable:qualifier} = 'STATEMENT')""";

	public ExerciseTable(final DeferredDatabase database) {
		super(database);
	}

	@Query(distinct = true, condition = ExerciseTable.hasSolutionAndStatement)
	public abstract List<ExerciseData> allWithSolution();

	@Query(
			distinct = true,
			tables = { @Table(typeName = ExamTable.class), @Table(typeName = SubjectTable.class) },
			condition = ExerciseTable.hasSolutionAndStatement
	)
	public abstract List<ExerciseData> withSolutionAnySubject(@Param Collection<SubjectData> subjects);

	@Query(
			distinct = true,
			tables = {
					@Table(typeName = ExerciseAttachmentTable.class),
					@Table(columns = { @SelectColumn(name = "name") }, typeName = ExamAttachmentTable.class),
					@Table(typeName = ExamTable.class, on = "{M:ExamAttachmentTable:examId} = {M:ExamTable:id}"),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class),
					@Table(typeName = TagTable.class)

			},
			condition = ExerciseTable.hasSolutionAndStatement
//			groupBy = { "{M:ExerciseTable:id}", "{M:ExamAttachmentTable:name}" }
	)
	public abstract List<ExerciseData> withSolutionAnySubjectAnyTag(
			@Param Collection<SubjectData> subjects,
			@Param(value = "{M:TagTable:name}", ignoreNull = true) Collection<String> tagData);

	@Query(
			distinct = true,
			tables = {
					@Table(typeName = ExerciseAttachmentTable.class),
					@Table(columns = { @SelectColumn(name = "name") }, typeName = ExamAttachmentTable.class),
					@Table(typeName = ExamTable.class, on = "{M:ExamAttachmentTable:examId} = {M:ExamTable:id}"),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class),
					@Table(typeName = TagTable.class)

			},
			condition = ExerciseTable.hasSolutionAndStatement
//			groupBy = { "{M:ExerciseTable:id}", "{M:ExamAttachmentTable:name}" }
	)
	public abstract List<ExerciseData> withSolutionAnySubjectAllTags(
			@Param Collection<SubjectData> subjects,
			@All @Param(value = "{M:TagTable:name}", ignoreNull = true) Collection<String> tagData,
			@Limit int limit);

	@Query(
			distinct = true,
			tables = {
					@Table(typeName = ExerciseAttachmentTable.class),
					@Table(columns = { @SelectColumn(name = "name") }, typeName = ExamAttachmentTable.class),
					@Table(typeName = ExamTable.class, on = "{M:ExamAttachmentTable:examId} = {M:ExamTable:id}"),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class),
					@Table(typeName = TagTable.class)

			}
//			groupBy = { "{M:ExerciseTable:id}", "{M:ExamAttachmentTable:name}" }
	)
	public abstract List<ExerciseData> byAnySubjectAllTags(
			@Param Collection<SubjectData> subjects,
			@All @Param(value = "{M:TagTable:name}", ignoreNull = true) Collection<String> tagData,
			@Limit int limit);

	@Query(
			distinct = true,
			tables = {
					@Table(typeName = ExerciseAttachmentTable.class),
					@Table(columns = { @SelectColumn(name = "name") }, typeName = ExamAttachmentTable.class),
					@Table(typeName = ExamTable.class, on = "{M:ExamAttachmentTable:examId} = {M:ExamTable:id}"),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class),
					@Table(typeName = TagTable.class),
					@Table(typeName = UserExerciseTable.class, join = Table.Type.LEFT),
					@Table(typeName = UserTable.class, join = Table.Type.LEFT) },
			condition = hasSolutionAndStatement
//			groupBy = { "{M:ExerciseTable:id}", "{M:ExamAttachmentTable:name}" }
	)
	public abstract List<ExerciseData> withSolutionAnySubjectAllTagsNotByStatus(
			@Param Collection<SubjectData> subjects,
			@All @Param(value = "{M:TagTable:name}", ignoreNull = true) Collection<String> tagData,
			@Param @OrIsNull UserData userData,
			@Param(comparator = "IS DISTINCT FROM") ExerciseStatus status,
			@Limit int limit);

	@Query(
			distinct = true,
			tables = {
					@Table(typeName = ExerciseAttachmentTable.class),
					@Table(columns = { @SelectColumn(name = "name") }, typeName = ExamAttachmentTable.class),
					@Table(typeName = ExamTable.class, on = "{M:ExamAttachmentTable:examId} = {M:ExamTable:id}"),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class),
					@Table(typeName = TagTable.class),
					@Table(typeName = UserExerciseTable.class, join = Table.Type.LEFT),
					@Table(typeName = UserTable.class, join = Table.Type.LEFT) }
//			groupBy = { "{M:ExerciseTable:id}", "{M:ExamAttachmentTable:name}" }
//			condition = "{M:UserExerciseTable:status} NOT IN {V:status} OR {M:UserExerciseTable:status} IS NULL"
	)
	public abstract List<ExerciseData> byAnySubjectAllTagsNotByStatus(
			@Param Collection<SubjectData> subjects,
			@All @Param(value = "{M:TagTable:name}", ignoreNull = true) Collection<String> tagData,
			@Param @OrIsNull UserData userData,
			@Param(comparator = "IS DISTINCT FROM") ExerciseStatus status,
			@Limit int limit);

}
