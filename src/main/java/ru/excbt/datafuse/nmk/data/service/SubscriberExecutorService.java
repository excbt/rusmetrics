/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Executor;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.service.support.SubscriberExecutorsHolder;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.12.2016
 * 
 */
@Service
public class SubscriberExecutorService {

	private static final Logger logger = LoggerFactory.getLogger(SubscriberExecutorService.class);

	private final SubscriberExecutorsHolder subscriberExecutors = new SubscriberExecutorsHolder();

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public Executor getExecutor(Long subscriberId) {
		checkNotNull(subscriberId);
		return subscriberExecutors.getSubscriberExecutor(subscriberId);
	}

	/**
	 * 
	 */
	@PreDestroy
	private void shutdown() {
		logger.info("Shutdown Subscriber executor service");
		subscriberExecutors.shutDown();
	}

}
