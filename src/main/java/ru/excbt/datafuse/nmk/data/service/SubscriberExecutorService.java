/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

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
	public Future<Boolean> submit(Long subscriberId, Runnable task) {
		checkNotNull(subscriberId);
		checkNotNull(task);
		return subscriberExecutors.submit(subscriberId, task);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param task
	 * @return
	 */
	public Future<Boolean> submit(Long subscriberId, Callable<Boolean> task) {
		checkNotNull(subscriberId);
		checkNotNull(task);
		return subscriberExecutors.submit(subscriberId, task);
	}

	/**
	 * @throws InterruptedException
	 * 
	 */
	@PreDestroy
	private void shutdown() throws InterruptedException {
		logger.info("Shutdown Subscriber executor service");
		subscriberExecutors.shutDown();
	}

}
