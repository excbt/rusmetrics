package ru.excbt.datafuse.nmk.data.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.utils.ResourceHelper;

@Service
@PropertySource(value = "classpath:META-INF/app-version.properties")
public class AppVersionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppVersionService.class);

	@Value("${app.version}")
	private String appVersion;

	public class AppModuleInfo {
		private String moduleKeyname;
		private String moduleName;
		private String moduleVersion;
		private String moduleReleaseDate;

		public String getModuleVersion() {
			return moduleVersion;
		}

		public String getModuleKeyname() {
			return moduleKeyname;
		}

		public String getModuleName() {
			return moduleName;
		}

		public String getModuleReleaseDate() {
			return moduleReleaseDate;
		}
	}

	/**
	 *
	 * @return
	 */
	public String getAppVersion() {
		return appVersion;
	}

	/**
	 *
	 * @return
	 */
	public Properties getAppVersionProps() {

		final String filePath = "META-INF/app-version.properties";

		File f = ResourceHelper.findResource(filePath);

		Properties properties = new Properties();

		if (f.exists()) {
			try (final InputStream stream = new FileInputStream(f)) {
				try {
					properties.load(stream);
				} catch (IOException e) {
					LOGGER.warn("Can't load appVersion properties from {}", filePath);
				}
			} catch (FileNotFoundException e1) {
			} catch (IOException e1) {
			}

		}
		return properties;
	}

	/**
	 *
	 * @return
	 */
	public List<AppModuleInfo> getAppModulesInfo() {

		List<AppModuleInfo> result = new ArrayList<>();

		Properties props = getAppVersionProps();

		String modulesValue = props.getProperty("app.modules");
		String[] modules = modulesValue.split(",");

		for (String keyname : modules) {
			AppModuleInfo info = new AppModuleInfo();
			info.moduleKeyname = keyname;
			info.moduleVersion = props.getProperty(keyname + ".version");
			info.moduleName = props.getProperty(keyname + ".name");
			info.moduleReleaseDate = props.getProperty(keyname + ".releaseDate");
			result.add(info);
		}

		return result;
	}

}
