package ru.excbt.datafuse.nmk.web.service;

import java.io.File;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebAppPropsService {

	public final static String CATALINA_BASE = "catalina.base";
	public final static String HWATERS_CSV_OUT = "app.hwaters.csv.out";
	
	
	@Autowired
	private ServletContext servletContext;

	/**
	 * 
	 * @return
	 */
	public String getWebAppHomeDirectory() {
		return servletContext.getRealPath("/");
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getServerHomeDirectory() {
		String catalinaBase = System.getProperty(CATALINA_BASE);
		File f = new File(".");
		return catalinaBase != null ? catalinaBase : f.getAbsolutePath();
	}	
	
	/**
	 * 
	 * @return
	 */
	public String getHWatersCvsOut() {
		return getServerHomeDirectory() + File.separator + HWATERS_CSV_OUT;
	}	
}
