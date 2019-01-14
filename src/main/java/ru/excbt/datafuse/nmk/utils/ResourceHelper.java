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
    public static File findResource(final String resourcePath) {

        File resourceFile;

        resourceFile = new File(resourcePath);
        if (resourceFile.exists()) {
            return resourceFile;
        }

        final String cleanResourcePath = resourcePath.replaceAll("^/|/$","");

        String userDir = System.getProperty("user.dir");

        resourceFile = new File(userDir + File.separator + cleanResourcePath);

        if (resourceFile.exists()) {
            return resourceFile;
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        URL classLoaderUrl = classLoader.getResource(".");
        URL classLoaderResourceUrl = classLoader.getResource(cleanResourcePath);

        File f;
        if (classLoaderResourceUrl == null) {
            f = new File(classLoaderUrl.getPath() + cleanResourcePath);
        } else {
            f = new File(classLoaderResourceUrl.getPath());
        }

        return f;
    }
}
