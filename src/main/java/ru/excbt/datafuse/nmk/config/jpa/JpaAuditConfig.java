package ru.excbt.datafuse.nmk.config.jpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import ru.excbt.datafuse.nmk.data.auditor.AuditorAwareImpl;
import ru.excbt.datafuse.nmk.data.model.AuditUser;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
	
	@Bean
	public AuditorAware<AuditUser> auditorProvider() {
		return new AuditorAwareImpl();
	}

}
