package lu.kbra.school_lu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lu.kbra.pclib.db.hook.VersionDbRule;
import lu.kbra.pclib.db.utils.DatabaseQueryableHookTemplate;

@Configuration
public class DbConfig {

	@Bean
	DatabaseQueryableHookTemplate queryableHookTemplate() {
		return new DatabaseQueryableHookTemplate().add(new VersionDbRule(true));
	}

}
