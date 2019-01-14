/**
 *
 */
package ru.excbt.datafuse.nmk.slog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.config.PortalProperties;
import ru.excbt.datafuse.nmk.config.SLogProperties;
import ru.excbt.datafuse.slogwriter.service.SLogService;
import ru.excbt.datafuse.slogwriter.service.SLogSessionT1;
import ru.excbt.datafuse.slogwriter.service.SLogSessionTN;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

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
    public SLogWriterService(PortalProperties portalProperties,
                             SLogProperties sLogProperties,
                             @Qualifier("dataSource") DataSource dataSource) throws IOException {
        log.info("SLogWriterService");
        log.info("URL: {}", portalProperties.getDatasource().getUrl());
        log.info("SCHEMA: {}", sLogProperties.getSettings().getSchema());
        Properties props = new Properties();
        props.put("dataSource.username", portalProperties.getDatasource().getUsername());
        props.put("dataSource.password", portalProperties.getDatasource().getPassword());
        props.put("dataSource.driverClassName", portalProperties.getDatasource().getDriverClassName());
        props.put("dataSource.url", portalProperties.getDatasource().getUrl());
        props.put("slog.schema", sLogProperties.getSettings().getSchema());
        this.sLogService = SLogService.newSLogService(props, dataSource);
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
     * @param deviceObjectId
     * @param sessionMessage
     * @param authorId
     * @return
     */
	public SLogSessionT1 newSessionWebT1(Long dataSourceId, Long deviceObjectId, String sessionMessage, Long authorId) {
		return sLogService.newWebSessionT1(dataSourceId, deviceObjectId, sessionMessage, authorId);
	}

	public SLogSessionTN newSessionWebTN(Long dataSourceId, Long deviceObjectId, String sessionMessage, Long authorId) {
		return sLogService.newWebSessionTN();
	}

}
