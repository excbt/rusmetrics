package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;

public final class SubscriberParam {

	private final long subscriberId;

	/**
	 * 
	 * @param subscriberId
	 */
	public SubscriberParam(Long subscriberId) {
		checkNotNull(subscriberId, "subscriberId is NULL");
		this.subscriberId = subscriberId;
	}

	/**
	 * 
	 * @return
	 */
	public long getSubscriberId() {
		return subscriberId;
	}
}
