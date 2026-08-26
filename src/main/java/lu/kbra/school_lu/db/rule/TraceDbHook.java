package lu.kbra.school_lu.db.rule;

import java.sql.Statement;
import java.util.Arrays;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.hook.RuleHookType;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.BeforeRule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(5)
@Profile("debug")
public class TraceDbHook implements BeforeRule {

	private static final int longest = Arrays.stream(RuleHookType.values())
			.mapToInt(hookType -> hookType.name().substring(hookType.name().indexOf("_") + 1).length())
			.max()
			.orElse(0);

	@Override
	public void executeBefore(final RuleHookType hookType, final SQLQueryable<?> queryable, final Statement pstmt, final Object data) {
		TraceDbHook.log.info(PCUtils.rightPadString(hookType.name().substring(hookType.name().indexOf("_") + 1), " ", longest) + " | "
				+ PCUtils.getStatementAsSQL(pstmt));
	}

	@Override
	public boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
		return hookType.isBefore();
	}

}
