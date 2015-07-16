package ru.excbt.datafuse.nmk.web.service;

import java.io.File;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.service.support.HWatersCsvProps;

@Service
public class WebAppPropsService implements HWatersCsvProps {

	public final static String CATALINA_BASE = "catalina.base";

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
	@Override
	public String getHWatersCsvOutputDir() {
		return getServerHomeDirectory() + File.separator + HWATERS_CSV_OUT;
	}

	/**
	 * 
	 */
	@Override
	public String getHWatersCsvInputDir() {
		return getServerHomeDirectory() + File.separator + HWATERS_CSV_IN;
	}
}
