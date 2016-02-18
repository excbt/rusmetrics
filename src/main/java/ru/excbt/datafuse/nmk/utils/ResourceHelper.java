package ru.excbt.datafuse.nmk.utils;

import java.io.File;
import java.net.URL;

/**
 * Работа с ресурсами
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.04.2015
 *
 */
public class ResourceHelper {
	/**
	 * 
	 * @param resourcePath
	 * @return
	 */
	public static File findResource(String resourcePath) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		URL startDir = classLoader.getResource(".");
		URL urlResource = classLoader.getResource(resourcePath);

		File f;
		if (urlResource == null) {
			f = new File(startDir.getPath() + resourcePath);
		} else {
			f = new File(urlResource.getPath());
		}

		return f;
	}
}
