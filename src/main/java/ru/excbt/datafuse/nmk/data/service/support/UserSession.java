package ru.excbt.datafuse.nmk.data.service.support;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession {

	private SubscriberParam subscriberParam;

	public SubscriberParam getSubscriberParam() {
		return subscriberParam;
	}

	public void setSubscriberParam(SubscriberParam subscriberParam) {
		this.subscriberParam = subscriberParam;
	}

}
