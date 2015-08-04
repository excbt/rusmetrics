package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import ru.excbt.datafuse.nmk.data.domain.StatusColorObject;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKey;

public class MonitorContEventNotificationStatus implements Serializable,
		StatusColorObject, ContObjectHolder {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5870004714222214147L;

	private final ContObject contObject;

	private ContEventLevelColorKey contEventLevelColorKey;

	private long eventsCount;

	private long eventsTypesCount;

	private long newEventsCount;

	public MonitorContEventNotificationStatus(ContObject contObject) {
		this.contObject = ContObjectShort.newInstance(contObject);
	}

	@Override
	public ContObject getContObject() {
		return contObject;
	}

	public static MonitorContEventNotificationStatus newInstance(
			ContObject contObject) {
		checkNotNull(contObject);
		MonitorContEventNotificationStatus result = new MonitorContEventNotificationStatus(
				contObject);
		return result;
	}

	public ContEventLevelColorKey getContEventLevelColorKey() {
		return contEventLevelColorKey;
	}

	public void setContEventLevelColorKey(
			ContEventLevelColorKey contEventLevelColorKey) {
		this.contEventLevelColorKey = contEventLevelColorKey;
	}

	@Override
	public String getStatusColor() {
		return contEventLevelColorKey == null ? null : contEventLevelColorKey
				.getKeyname();
	}

	public long getEventsCount() {
		return eventsCount;
	}

	public void setEventsCount(long eventsCount) {
		this.eventsCount = eventsCount;
	}

	public long getEventsTypesCount() {
		return eventsTypesCount;
	}

	public void setEventsTypesCount(long eventsTypesCount) {
		this.eventsTypesCount = eventsTypesCount;
	}

	public long getNewEventsCount() {
		return newEventsCount;
	}

	public void setNewEventsCount(long newEventsCount) {
		this.newEventsCount = newEventsCount;
	}

}
