package lu.kbra.school_lu.db.table;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.All;
import lu.kbra.pclib.db.annotations.query.Limit;
import lu.kbra.pclib.db.annotations.query.OrIsNull;
import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.OrderBy;
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

	private static final String randomButOldest = "{F:rand}() * (1.0 + {F:coalesce}(extract(epoch from (current_timestamp - {M:UserExerciseTable:timestamp})) / 86400.0, 30.0))";

	public ExerciseTable(final DeferredDatabase database) {
		super(database);
	}

	@Query(condition = ExerciseTable.hasSolutionAndStatement, orderBy = { @OrderBy(value = "{F:rand}()", type = OrderBy.Type.NONE) })
	public abstract List<ExerciseData> allWithSolution();

	@Query(
			tables = {
					@Table(typeName = ExamPartTable.class),
					@Table(typeName = ExamPartExamTable.class),
					@Table(typeName = ExamTable.class),
					@Table(typeName = SubjectTable.class) },
			condition = ExerciseTable.hasSolutionAndStatement,
			orderBy = { @OrderBy(value = randomButOldest, type = OrderBy.Type.DESC) }
	)
	public abstract List<ExerciseData> withSolutionAnySubject(@Param Collection<SubjectData> subjects);

	@Query(
			tables = {
					@Table(typeName = ExamPartTable.class),
					@Table(typeName = ExamPartExamTable.class),
					@Table(typeName = ExamTable.class),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class, join = Table.Type.LEFT),
					@Table(typeName = TagTable.class, join = Table.Type.LEFT)

			},
			condition = ExerciseTable.hasSolutionAndStatement,
			orderBy = { @OrderBy(value = randomButOldest, type = OrderBy.Type.DESC) }
	)
	public abstract List<ExerciseData> withSolutionAnySubjectAnyTag(
			@Param Collection<SubjectData> subjects,
			@Param(value = "{M:TagTable:name}", ignoreNull = true) Collection<String> tagData);

	@Query(
			tables = {
					@Table(typeName = ExamPartTable.class),
					@Table(typeName = ExamPartExamTable.class),
					@Table(typeName = ExamTable.class),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class, join = Table.Type.LEFT),
					@Table(typeName = TagTable.class, join = Table.Type.LEFT)

			},
			condition = ExerciseTable.hasSolutionAndStatement,
			orderBy = { @OrderBy(value = randomButOldest, type = OrderBy.Type.DESC) }
	)
	public abstract List<ExerciseData> withSolutionAnySubjectAllTags(
			@Param Collection<SubjectData> subjects,
			@All @Param(value = "{M:TagTable:name}", ignoreNull = true) Collection<String> tagData,
			@Limit int limit);

	@Query(
			tables = {
					@Table(typeName = ExamPartTable.class),
					@Table(typeName = ExamPartExamTable.class),
					@Table(typeName = ExamTable.class),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class, join = Table.Type.LEFT),
					@Table(typeName = TagTable.class, join = Table.Type.LEFT)

			},
			orderBy = { @OrderBy(value = randomButOldest, type = OrderBy.Type.DESC) }
	)
	public abstract List<ExerciseData> byAnySubjectAllTags(
			@Param Collection<SubjectData> subjects,
			@All @Param(value = "{M:TagTable:name}", ignoreNull = true) Collection<String> tagData,
			@Limit int limit);

	@Query(
			tables = {
					@Table(typeName = ExamPartTable.class),
					@Table(typeName = ExamPartExamTable.class),
					@Table(typeName = ExamTable.class),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class, join = Table.Type.LEFT),
					@Table(typeName = TagTable.class, join = Table.Type.LEFT),
					@Table(typeName = UserExerciseTable.class, join = Table.Type.LEFT),
					@Table(typeName = UserTable.class, join = Table.Type.LEFT) },
			condition = hasSolutionAndStatement,
			orderBy = { @OrderBy(value = randomButOldest, type = OrderBy.Type.DESC) }
	)
	public abstract List<ExerciseData> withSolutionAnySubjectAllTagsNotByStatus(
			@Param Collection<SubjectData> subjects,
			@All @Param(value = "{M:TagTable:name}", ignoreNull = true) Collection<String> tagData,
			@Param @OrIsNull UserData userData,
			@Param(comparator = "IS DISTINCT FROM", ignoreNull = true) ExerciseStatus status,
			@Limit int limit);

	@Query(
			tables = {
					@Table(typeName = ExamPartTable.class),
					@Table(typeName = ExamPartExamTable.class),
					@Table(typeName = ExamTable.class),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class, join = Table.Type.LEFT),
					@Table(typeName = TagTable.class, join = Table.Type.LEFT),
					@Table(typeName = UserExerciseTable.class, join = Table.Type.LEFT),
					@Table(typeName = UserTable.class, join = Table.Type.LEFT) },
			orderBy = { @OrderBy(value = randomButOldest, type = OrderBy.Type.DESC) }
	)
	public abstract List<ExerciseData> byAnySubjectAllTagsNotByStatus(
			@Param Collection<SubjectData> subjects,
			@All @Param(value = "{M:TagTable:name}", ignoreNull = true) Collection<String> tagData,
			@Param @OrIsNull UserData userData,
			@Param(comparator = "IS DISTINCT FROM", ignoreNull = true) ExerciseStatus status,
			@Limit int limit);

	/* COUNT */

	@Query(
			retColumns = { "{F:count}({M:ExerciseTable:id}) AS {Q:count}" },
			tables = {
					@Table(typeName = ExamPartTable.class),
					@Table(typeName = ExamPartExamTable.class),
					@Table(typeName = ExamTable.class),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class, join = Table.Type.LEFT),
					@Table(typeName = TagTable.class, join = Table.Type.LEFT) },
			condition = hasSolutionAndStatement
	)
	public abstract int countWithSolutionAnySubjectAllTags(
			@Param Collection<SubjectData> subjects,
			@All @Param(value = "{M:TagTable:name}", ignoreNull = true) Collection<String> tagData);

	@Query(
			retColumns = { "{F:count}({M:ExerciseTable:id}) AS {Q:count}" },
			tables = {
					@Table(typeName = ExamPartTable.class),
					@Table(typeName = ExamPartExamTable.class),
					@Table(typeName = ExamTable.class),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class, join = Table.Type.LEFT),
					@Table(typeName = TagTable.class, join = Table.Type.LEFT) }
	)
	public abstract int countByAnySubjectAllTags(
			@Param Collection<SubjectData> subjects,
			@All @Param(value = "{M:TagTable:name}", ignoreNull = true) Collection<String> tagData);

	@Query(
			retColumns = { "{F:count}({M:ExerciseTable:id}) AS {Q:count}" },
			tables = {
					@Table(typeName = ExamPartTable.class),
					@Table(typeName = ExamPartExamTable.class),
					@Table(typeName = ExamTable.class),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class, join = Table.Type.LEFT),
					@Table(typeName = TagTable.class, join = Table.Type.LEFT),
					@Table(typeName = UserExerciseTable.class, join = Table.Type.LEFT),
					@Table(typeName = UserTable.class, join = Table.Type.LEFT) },
			condition = hasSolutionAndStatement
	)
	public abstract int countWithSolutionAnySubjectAllTagsNotByStatus(
			@Param Collection<SubjectData> subjects,
			@All @Param(value = "{M:TagTable:name}", ignoreNull = true) Collection<String> tagData,
			@Param @OrIsNull UserData userData,
			@Param(comparator = "IS DISTINCT FROM", ignoreNull = true) ExerciseStatus status);

	@Query(
			retColumns = { "{F:count}({M:ExerciseTable:id}) AS {Q:count}" },
			tables = {
					@Table(typeName = ExamPartTable.class),
					@Table(typeName = ExamPartExamTable.class),
					@Table(typeName = ExamTable.class),
					@Table(typeName = SubjectTable.class),
					@Table(typeName = ExerciseTagTable.class, join = Table.Type.LEFT),
					@Table(typeName = TagTable.class, join = Table.Type.LEFT),
					@Table(typeName = UserExerciseTable.class, join = Table.Type.LEFT),
					@Table(typeName = UserTable.class, join = Table.Type.LEFT) }
	)
	public abstract int countByAnySubjectAllTagsNotByStatus(
			@Param Collection<SubjectData> subjects,
			@All @Param(value = "{M:TagTable:name}", ignoreNull = true) Collection<String> tagData,
			@Param @OrIsNull UserData userData,
			@Param(comparator = "IS DISTINCT FROM", ignoreNull = true) ExerciseStatus status);

}
