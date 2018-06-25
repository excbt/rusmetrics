package ru.excbt.datafuse.nmk.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.excbt.datafuse.nmk.config.DatabaseConfiguration;
import ru.excbt.datafuse.nmk.config.ldap.LdapConfig;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractDBToolCli {

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractDBToolCli.class);

	protected ApplicationContext appContext;

	@PersistenceContext
	protected EntityManager entityManager;

	// @PersistenceContext(unitName="dataraw")
	// protected EntityManager entityManagerRaw;

	public void autowireBeans() {
		AutowireCapableBeanFactory acbFactory = appContext
				.getAutowireCapableBeanFactory();
		acbFactory.autowireBean(this);
	}

	public AbstractDBToolCli() {
		appContext = new AnnotationConfigApplicationContext(
				DatabaseConfiguration.class, LdapConfig.class);
	}

	public void showAppStatus() {
		logger.info("Application Started");
		logger.info("Status: {}",
				entityManager != null ? "entityManager inititalized"
						: "entityManager NOT inititalized");
	}
}
