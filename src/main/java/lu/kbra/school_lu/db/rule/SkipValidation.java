package lu.kbra.school_lu.db.rule;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.queryable.QueryableHint;

@Documented
@Retention(RUNTIME)
@Target(TYPE_USE)
public @interface SkipValidation {

	@QueryableHint(type = ValidationRule.SKIP_VALIDATION)
	boolean value() default true;

}
