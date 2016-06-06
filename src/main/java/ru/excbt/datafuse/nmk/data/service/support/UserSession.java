package ru.excbt.datafuse.nmk.data.service.support;

//@Component
//@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession {

	private SubscriberParam subscriberParam;

	public SubscriberParam getSubscriberParam() {
		return subscriberParam;
	}

	public void setSubscriberParam(SubscriberParam subscriberParam) {
		this.subscriberParam = subscriberParam;
	}

}
