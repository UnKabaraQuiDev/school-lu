package lu.kbra.school_lu.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.data.ExamSeason;
import lu.kbra.school_lu.data.ExamType;
import lu.kbra.school_lu.db.data.ExamData;
import lu.kbra.school_lu.db.data.ExerciseData;

@Component
public abstract class ExamTable extends DeferredDatabaseTable<ExamData> {

	public ExamTable(final DeferredDatabase database) {
		super(database);
	}

	@Query(
			tables = {
					@Table(typeName = ExamPartExamTable.class),
					@Table(typeName = ExamPartTable.class),
					@Table(typeName = ExerciseTable.class) }
	)
	public abstract ExamData byExercise(@Param ExerciseData c);

	@Query(tables = { @Table(typeName = SubjectTable.class), @Table(typeName = SectionTable.class) })
	public abstract ExamData bySectionSubjectYearSeasonSubtype(
			@Param("{M:SectionTable:name}") String sectionName,
			@Param("{M:SubjectTable:name}") String subjectName,
			@Param int year,
			@Param ExamSeason season,
			@Param ExamType subtype);

}
