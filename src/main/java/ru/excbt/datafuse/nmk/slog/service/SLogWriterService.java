/**
 * 
 */
package ru.excbt.datafuse.nmk.slog.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.slogwriter.service.SLogService;
import ru.excbt.datafuse.slogwriter.service.SLogSessionTN;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2016
 * 
 */
@Service
public class SLogWriterService {

	private static final Logger logger = LoggerFactory.getLogger(SLogWriterService.class);

	private SLogService sLogService;

	/**
	 * 
	 * @throws IOException
	 */
	public SLogWriterService() throws IOException {
		Resource resource = new ClassPathResource("/META-INF/slogwriter-data-access.properties");
		Properties props = PropertiesLoaderUtils.loadProperties(resource);
		checkNotNull(props);
		checkState(props.size() > 0, "SLogWriterService cannot be initialized");
		this.sLogService = SLogService.newSLogService(props);
	}

	/**
	 * 
	 */
	@PreDestroy
	private void shutdown() {
		sLogService.shutdown();
	}

	/**
	 * 
	 * @param dataSourceId
	 * @param deviceId
	 * @param sessionMessage
	 * @return
	 */
	public SLogSessionTN newSessionWebT1(Long authorId) {
		return sLogService.newWebSessionTN();
	}

}
