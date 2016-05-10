package ru.excbt.datafuse.nmk.data.service.support;

public final class SubscriberParam {

	private final long subscriberId;

	public SubscriberParam(long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public long getSubscriberId() {
		return subscriberId;
	}
}
