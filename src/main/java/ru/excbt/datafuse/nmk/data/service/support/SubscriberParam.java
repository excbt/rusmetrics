package ru.excbt.datafuse.nmk.data.service.support;

import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;

public final class SubscriberParam {

	private final long subscriberId;
	private final long subscrUserId;
	private final boolean isRma;
	private final long rmaSubscriberId;
	private final long parentSubscriberId;
	private final SubscrTypeKey subscrTypeKey;

	public final static class Builder {
		private long subscriberId;
		private long subscrUserId;
		private boolean isRma;
		private Long rmaSubscriberId;
		private Long parentSubscriberId;
		private SubscrTypeKey subscrTypeKey;

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

		public Builder rmaSubscriber(Long rmaSubscriberId) {
			this.rmaSubscriberId = rmaSubscriberId;
			return this;
		}

		public Builder parentSubscriber(Long parentSubscriberId) {
			this.parentSubscriberId = parentSubscriberId;
			return this;
		}

		public Builder subscrTypeKey(String subscrType) {
			SubscrTypeKey key = SubscrTypeKey.searchKeyname(subscrType);
			this.subscrTypeKey = key != null ? key : SubscrTypeKey.NORMAL;
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
		this.rmaSubscriberId = builder.rmaSubscriberId != null ? builder.rmaSubscriberId : 0;
		this.parentSubscriberId = builder.parentSubscriberId != null ? builder.parentSubscriberId : 0;
		this.subscrTypeKey = builder.subscrTypeKey;
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

	/**
	 * 
	 * @return
	 */
	public boolean isRma() {
		return isRma;
	}

	/**
	 * 
	 * @return
	 */
	public SubscrTypeKey getSubscrTypeKey() {
		return subscrTypeKey;
	}

	/**
	 * 
	 * @return
	 */
	public long getRmaSubscriberId() {
		if (isRma) {
			return subscriberId;
		}
		return rmaSubscriberId;
	}

	/**
	 * 
	 * @return
	 */
	public long getParentSubscriberId() {
		return parentSubscriberId;
	}

	/**
	 * 
	 * @return
	 */
	public boolean haveParentSubacriber() {
		return this.parentSubscriberId != 0;
	}

	/**
	 * 
	 * @return
	 */
	public boolean haveRmaSubacriber() {
		return this.rmaSubscriberId != 0;
	}

	@Override
	public String toString() {
		StringBuilder builder2 = new StringBuilder();
		builder2.append("SubscriberParam [subscriberId=");
		builder2.append(subscriberId);
		builder2.append(", subscrUserId=");
		builder2.append(subscrUserId);
		builder2.append(", isRma=");
		builder2.append(isRma);
		builder2.append(", rmaSubscriberId=");
		builder2.append(rmaSubscriberId);
		builder2.append(", parentSubscriberId=");
		builder2.append(parentSubscriberId);
		builder2.append(", subscrTypeKey=");
		builder2.append(subscrTypeKey);
		builder2.append("]");
		return builder2.toString();
	}

}
