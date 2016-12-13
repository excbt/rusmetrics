/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.12.2016
 * 
 */
public class SubscriberExecutorsHolder {

	private class ExecutorHolder {
		private final ExecutorService executorService = Executors.newFixedThreadPool(1);
		//private final AtomicLong taskCounter = new AtomicLong();
	}

	private final ConcurrentHashMap<Long, ExecutorHolder> subscriberExecutors;

	private final AtomicBoolean isShutdown;

	public SubscriberExecutorsHolder() {
		this.subscriberExecutors = new ConcurrentHashMap<>();
		this.isShutdown = new AtomicBoolean();
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public Executor getSubscriberExecutor(Long subscriberId) {

		if (isShutdown.get() == true) {
			throw new IllegalStateException();
		}

		ExecutorHolder executorHolder = subscriberExecutors.get(subscriberId);
		if (executorHolder == null) {

			synchronized (subscriberExecutors) {
				executorHolder = subscriberExecutors.get(subscriberId);
				if (executorHolder == null) {
					executorHolder = new ExecutorHolder();
					subscriberExecutors.put(subscriberId, executorHolder);
				}
			}

		}
		return executorHolder.executorService;
	}

	/**
	 * 
	 */
	public void shutDown() {
		if (isShutdown.get() == true) {
			throw new IllegalStateException();
		}
		synchronized (subscriberExecutors) {
			for (ExecutorHolder holder : subscriberExecutors.values()) {
				holder.executorService.shutdown();
			}
			isShutdown.set(true);
		}
	}

}
