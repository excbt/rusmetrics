package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import ru.excbt.datafuse.nmk.data.domain.StatusColorObject;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKey;

public class MonitorContEventTypeStatus implements Serializable,
		StatusColorObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 922420708990860978L;

	private final ContEventType contEventType;

	private long totalCount;

	private ContEventLevelColorKey contEventLevelColorKey;

	public MonitorContEventTypeStatus(ContEventType contEventType) {
		this.contEventType = contEventType;
	}

	public static MonitorContEventTypeStatus newInstance(ContEventType contEventType) {
		checkNotNull(contEventType);
		MonitorContEventTypeStatus result = new MonitorContEventTypeStatus(
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
