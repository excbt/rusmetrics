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
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				logger.info("deregistering jdbc driver: {}", driver);
				DriverManager.deregisterDriver(driver);
			} catch (SQLException e) {
				logger.error("Error deregistering driver : {}", driver);
			}
		}
	}

}
