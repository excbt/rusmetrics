package ru.excbt.datafuse.nmk.data.model.support;

import ru.excbt.datafuse.nmk.data.model.Subscriber;

public interface SubscriberUser {

	public Long getId();

	public Subscriber getSubscriber();

	default public Long getSubscriberId() {
		return getSubscriber() != null ? getSubscriber().getId() : null;
	}
}
