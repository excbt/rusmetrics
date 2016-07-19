package ru.excbt.datafuse.nmk.config.mvc;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class MvcServletContextListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory
			.getLogger(MvcServletContextListener.class);

	/**
	 * 
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Starting NMK Web Application");
	}

	/**
	 * 
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("Stopping NMK Web Application");

		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();

			if (driver.getClass().getClassLoader() == cl) {

				try {
					logger.info("Deregistering JDBC driver {}", driver);
					DriverManager.deregisterDriver(driver);

				} catch (SQLException ex) {
					logger.error("Error deregistering JDBC driver {}", driver, ex);
				}

			} else {
				logger.trace("Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader",
						driver);
			}
		}

	}

}
