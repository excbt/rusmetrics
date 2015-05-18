package ru.excbt.datafuse.nmk.config.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
	
//	@Bean
//	public AuditorAware<AuditUser> auditorProvider() {
//		return new AuditorAwareImpl();
//	}

}
