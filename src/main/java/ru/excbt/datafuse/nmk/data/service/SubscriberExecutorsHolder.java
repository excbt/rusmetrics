/**
 *
 */
package ru.excbt.datafuse.nmk.data.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

	private CopyOnWriteArrayList<Future<Boolean>> taskResults = new CopyOnWriteArrayList<>();

	public SubscriberExecutorsHolder() {
		this.subscriberExecutors = new ConcurrentHashMap<>();
		this.isShutdown = new AtomicBoolean();
	}

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	protected ExecutorService getSubscriberExecutor(Long subscriberId) {

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
	 * @throws InterruptedException
	 *
	 */
	public void shutDown() throws InterruptedException {
		if (isShutdown.get() == true) {
			throw new IllegalStateException();
		}

		synchronized (subscriberExecutors) {

			while (true) {
				boolean active = taskResults.stream().filter(i -> !i.isDone()).findAny().isPresent();
				if (active == false) {
					break;
				}
				Thread.sleep(1000);
			}

			for (ExecutorHolder holder : subscriberExecutors.values()) {
				holder.executorService.shutdown();
			}
			isShutdown.set(true);
		}
	}

	/**
	 *
	 * @param subscriberId
	 * @param task
	 * @return
	 */
	public Future<Boolean> submit(Long subscriberId, Runnable task) {
		Future<Boolean> result = getSubscriberExecutor(subscriberId).submit(task, Boolean.TRUE);
		taskResults.add(result);
		taskResults.removeIf(i -> i.isDone());
		return result;
	}

	/**
	 *
	 * @param subscriberId
	 * @param task
	 * @return
	 */
	public Future<Boolean> submit(Long subscriberId, Callable<Boolean> task) {
		Future<Boolean> result = getSubscriberExecutor(subscriberId).submit(task);
		taskResults.add(result);
		taskResults.removeIf(i -> i.isDone());
		return result;
	}

}
