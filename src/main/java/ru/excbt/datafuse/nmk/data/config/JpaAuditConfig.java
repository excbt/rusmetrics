package ru.excbt.datafuse.nmk.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import ru.excbt.datafuse.nmk.data.auditor.AuditorAwareImpl;
import ru.excbt.datafuse.nmk.data.model.SystemUser;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
	
	@Bean
	public AuditorAware<SystemUser> auditorProvider() {
		return new AuditorAwareImpl();
	}

}
