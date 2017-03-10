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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.config.jpa.JpaConfigLocal;
import ru.excbt.datafuse.slogwriter.service.SLogService;
import ru.excbt.datafuse.slogwriter.service.SLogSessionT1;
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

	private static final Logger log = LoggerFactory.getLogger(SLogWriterService.class);

	private SLogService sLogService;

	/**
	 *
	 * @throws IOException
	 */
	@Autowired
	public SLogWriterService(JpaConfigLocal.SLogDBProps sLogDBProps) throws IOException {
	    log.info("SLogWriterService");
	    log.info("URL: {}", sLogDBProps.getUrl());
	    log.info("SCHEMA: {}", sLogDBProps.getSchema());
		Properties props = new Properties();
		props.put("dataSource.username",sLogDBProps.getUsername());
		props.put("dataSource.password",sLogDBProps.getPassword());
		props.put("dataSource.driverClassName",sLogDBProps.getDriverClassName());
		props.put("dataSource.url",sLogDBProps.getUrl());
		props.put("slog.schema",sLogDBProps.getSchema());
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
	public SLogSessionT1 newSessionWebT1(Long dataSourceId, Long deviceObjectId, String sessionMessage, Long authorId) {
		return sLogService.newWebSessionT1(dataSourceId, deviceObjectId, sessionMessage, authorId);
	}

	public SLogSessionTN newSessionWebTN(Long dataSourceId, Long deviceObjectId, String sessionMessage, Long authorId) {
		return sLogService.newWebSessionTN();
	}

}
