package ru.excbt.datafuse.nmk.data.service;

import java.io.File;

import org.springframework.stereotype.Service;

/**
 * Сервис для обработки путей к файлам приложения
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.07.2015
 *
 */
@Service
public class AppPropsService implements HWatersCsvProps {

	/**
	 *
	 * @return
	 */
	public String getAppHomeDirectory() {
		File f = new File(".");
		return f.getAbsolutePath();
	}

	@Override
	public String getHWatersCsvOutputDir() {
		return getAppHomeDirectory() + File.separator + HWATERS_CSV_OUT;
	}

	@Override
	public String getHWatersCsvInputDir() {
		return getAppHomeDirectory() + File.separator + HWATERS_CSV_IN;
	}

}
