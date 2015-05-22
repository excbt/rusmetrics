package ru.excbt.datafuse.nmk.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBToolCli extends AbstractDBToolCli {

	private static final Logger logger = LoggerFactory
			.getLogger(DBToolCli.class);

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		DBToolCli app = new DBToolCli();
		app.autowireBeans();
		logger.info("Application Started");
		logger.info("Status: {}",
				app.entityManager != null ? "entityManager inititalized"
						: "entityManager NOT inititalized");
	}
}
