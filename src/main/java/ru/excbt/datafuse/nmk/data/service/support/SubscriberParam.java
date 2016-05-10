package ru.excbt.datafuse.nmk.data.service.support;

public final class SubscriberParam {

	private final long subscriberId;
	private final long subscrUserId;

	public final static class Builder {
		private long subscriberId;
		private long subscrUserId;

		public Builder subscriberId(Long arg) {
			this.subscriberId = arg;
			return this;
		}

		public Builder subscrUserId(Long arg) {
			this.subscrUserId = arg;
			return this;
		}

		public SubscriberParam build() {
			return new SubscriberParam(subscriberId, subscrUserId);
		}

	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * 
	 * @param subscriberId
	 */
	private SubscriberParam(long subscriberId, long subscrUserId) {
		this.subscriberId = subscriberId;
		this.subscrUserId = subscrUserId;
	}

	/**
	 * 
	 * @return
	 */
	public long getSubscriberId() {
		return subscriberId;
	}

	/**
	 * 
	 * @return
	 */
	public long getSubscrUserId() {
		return subscrUserId;
	}

}
