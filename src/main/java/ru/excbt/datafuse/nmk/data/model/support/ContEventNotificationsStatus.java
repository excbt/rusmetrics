package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import ru.excbt.datafuse.nmk.data.constant.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.domain.StatusColorObject;
import ru.excbt.datafuse.nmk.data.model.ContObject;

public class ContEventNotificationsStatus implements Serializable, StatusColorObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5870004714222214147L;

	private final ContObject contObject;

	private ContEventLevelColorKey contEventLevelColorKey;

	private long totalCount;

	private long totalTypesCount;

	private long totalNewCount;

	public ContEventNotificationsStatus(ContObject contObject) {
		this.contObject = contObject;
	}

	public ContObject getContObject() {
		return contObject;
	}

	public static ContEventNotificationsStatus newInstance(
			ContObject contObject) {
		checkNotNull(contObject);
		ContEventNotificationsStatus result = new ContEventNotificationsStatus(
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
