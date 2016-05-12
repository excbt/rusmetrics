package ru.excbt.datafuse.nmk.data.service.support;

public final class SubscriberParam {

	private final long subscriberId;
	private final long subscrUserId;
	private final boolean isRma;

	public final static class Builder {
		private long subscriberId;
		private long subscrUserId;
		private boolean isRma;

		public Builder subscriberId(Long arg) {
			this.subscriberId = arg;
			return this;
		}

		public Builder subscrUserId(Long arg) {
			this.subscrUserId = arg;
			return this;
		}

		public Builder isRma(Boolean arg) {
			this.isRma = Boolean.TRUE.equals(arg);
			return this;
		}

		public SubscriberParam build() {
			return new SubscriberParam(this);
		}

	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * 
	 * @param subscriberId
	 */
	private SubscriberParam(Builder builder) {
		this.subscriberId = builder.subscriberId;
		this.subscrUserId = builder.subscrUserId;
		this.isRma = builder.isRma;
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

	public boolean isRma() {
		return isRma;
	}

}
