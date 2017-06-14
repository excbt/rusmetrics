package ru.excbt.datafuse.nmk.data.model.support;

import ru.excbt.datafuse.nmk.data.model.Subscriber;

public interface SubscriberUser {

	Long getId();

	Subscriber getSubscriber();

	default Long getSubscriberId() {
		return getSubscriber() != null ? getSubscriber().getId() : null;
	}
}
