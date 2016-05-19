package ru.excbt.datafuse.nmk.data.service.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource(value = "classpath:META-INF/app-version.properties")
public class AppVersionService {

	@Value("${app.version}")
	private String appVersion;

	/**
	 * 
	 * @return
	 */
	public String getAppVersion() {
		return appVersion;
	}

}
