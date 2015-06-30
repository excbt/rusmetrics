package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import ru.excbt.datafuse.nmk.data.constant.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.model.ContObject;

public class SubscrContEventNotificationsStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5870004714222214147L;

	private final ContObject contObject;

	private ContEventLevelColorKey contEventLevelColorKey;

	private long totalCount;

	private long totalTypesCount;

	private long totalNewCount;

	public SubscrContEventNotificationsStatus(ContObject contObject) {
		this.contObject = contObject;
	}

	public ContObject getContObject() {
		return contObject;
	}

	public static SubscrContEventNotificationsStatus newInstance(
			ContObject contObject) {
		checkNotNull(contObject);
		SubscrContEventNotificationsStatus result = new SubscrContEventNotificationsStatus(
				contObject);
		return result;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getTotalTypesCount() {
		return totalTypesCount;
	}

	public void setTotalTypesCount(long totalTypesCount) {
		this.totalTypesCount = totalTypesCount;
	}

	public long getTotalNewCount() {
		return totalNewCount;
	}

	public void setTotalNewCount(long totalNewCount) {
		this.totalNewCount = totalNewCount;
	}

	public ContEventLevelColorKey getContEventLevelColorKey() {
		return contEventLevelColorKey;
	}

	public void setContEventLevelColorKey(
			ContEventLevelColorKey contEventLevelColorKey) {
		this.contEventLevelColorKey = contEventLevelColorKey;
	}

	public String getStatusColor() {
		return contEventLevelColorKey == null ? null : contEventLevelColorKey
				.getKeyname();
	}

}
