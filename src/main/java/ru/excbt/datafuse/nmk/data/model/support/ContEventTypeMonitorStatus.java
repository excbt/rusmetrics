package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import ru.excbt.datafuse.nmk.data.constant.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.domain.StatusColorObject;
import ru.excbt.datafuse.nmk.data.model.ContEventType;

public class ContEventTypeMonitorStatus implements Serializable,
		StatusColorObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 922420708990860978L;

	private final ContEventType contEventType;

	private long totalCount;

	private ContEventLevelColorKey contEventLevelColorKey;

	public ContEventTypeMonitorStatus(ContEventType contEventType) {
		this.contEventType = contEventType;
	}

	public ContEventTypeMonitorStatus newInstance(ContEventType contEventType) {
		checkNotNull(contEventType);
		ContEventTypeMonitorStatus result = new ContEventTypeMonitorStatus(
				contEventType);
		return result;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public ContEventLevelColorKey getContEventLevelColorKey() {
		return contEventLevelColorKey;
	}

	public void setContEventLevelColorKey(
			ContEventLevelColorKey contEventLevelColorKey) {
		this.contEventLevelColorKey = contEventLevelColorKey;
	}

	public ContEventType getContEventType() {
		return contEventType;
	}

	@Override
	public String getStatusColor() {
		return contEventLevelColorKey == null ? null : contEventLevelColorKey
				.getKeyname();
	}
}
