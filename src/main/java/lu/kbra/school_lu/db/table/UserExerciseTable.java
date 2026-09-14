package lu.kbra.school_lu.db.table;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Any;
import lu.kbra.pclib.db.annotations.query.Limit;
import lu.kbra.pclib.db.annotations.query.Offset;
import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.school_lu.data.ExerciseStatus;
import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.db.data.UserExerciseData;

@Component
public abstract class UserExerciseTable extends DeferredDatabaseTable<UserExerciseData> {

	public UserExerciseTable(final DeferredDatabase database) {
		super(database);
	}

	@Query(retColumns = { "{F:count}({M:UserExerciseTable:exerciseId})" })
	public abstract int countByUser(@Param UserData userData);

	@Query(retColumns = { "{F:count}({M:UserExerciseTable:exerciseId})" })
	public abstract int countByUserAnyStatus(@Param UserData userData, @Param @Any Set<ExerciseStatus> status);

	@Query
	public abstract List<UserExerciseData> byUserAndOffsetAndLimit(@Param UserData userData, @Offset int offset, @Limit int limit);

	@Query
	public abstract List<UserExerciseData> byUserAndOffsetAndLimitAnyStatus(
			@Param UserData userData,
			@Param @Any Set<ExerciseStatus> status,
			@Offset int offset,
			@Limit int limit);

}
