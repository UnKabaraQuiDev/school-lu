package lu.kbra.school_lu.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import lu.kbra.pclib.db.hook.VersionDbRule;
import lu.kbra.pclib.db.utils.DatabaseQueryableHookTemplate;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule;

@Configuration
public class DbConfig {

	@Bean
	DatabaseQueryableHookTemplate queryableHookTemplateProd(final List<SQLQueryableRule> rules) {
		final DatabaseQueryableHookTemplate template = new DatabaseQueryableHookTemplate();
		rules.forEach(template::add);
		return template;
	}

	@Bean
	@Order(10)
	VersionDbRule versionDbRule() {
		return new VersionDbRule(true);
	}

}
