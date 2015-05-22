package ru.excbt.datafuse.nmk.cli;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ru.excbt.datafuse.nmk.config.PropertyConfig;
import ru.excbt.datafuse.nmk.config.jpa.JpaConfigCli;

public abstract class AbstractDBToolCli {

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractDBToolCli.class);

	protected ApplicationContext appContext;

	@PersistenceContext
	protected EntityManager entityManager;

	public void autowireBeans() {
		AutowireCapableBeanFactory acbFactory = appContext
				.getAutowireCapableBeanFactory();
		acbFactory.autowireBean(this);
	}

	public AbstractDBToolCli() {
		appContext = new AnnotationConfigApplicationContext(
				PropertyConfig.class, JpaConfigCli.class);
	}
}
