package lu.kbra.school_lu.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import lu.kbra.pclib.db.hook.VersionRule;
import lu.kbra.pclib.db.rule.TraceRule;
import lu.kbra.pclib.db.rule.ValidationRule;
import lu.kbra.pclib.db.utils.DatabaseQueryableHookTemplate;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule;
import lu.kbra.pclib.db.validation.TableValidatorFactory;

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
	VersionRule versionRule() {
		return new VersionRule(true);
	}

	@Bean
	@Order(5)
	@Profile("debug")
	TraceRule traceDbRule() {
		return new TraceRule();
	}

	@Bean
	@Order(5)
	@Profile("debug")
	ValidationRule validationDbRule(TableValidatorFactory tvf) {
		return new ValidationRule(tvf);
	}

}
