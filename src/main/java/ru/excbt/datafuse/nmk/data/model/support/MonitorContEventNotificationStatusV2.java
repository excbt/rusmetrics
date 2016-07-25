package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.markers.StatusColorObject;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;

public class MonitorContEventNotificationStatusV2 implements Serializable,
		StatusColorObject, ContObjectHolder {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5870004714222214147L;

	private final ContObject contObject;

	private ContEventLevelColorKeyV2 contEventLevelColorKey;

	private long eventsCount;

	private long eventsTypesCount;

	private long newEventsCount;

	public MonitorContEventNotificationStatusV2(ContObject contObject) {
		this.contObject = ContObjectShort.newInstance(contObject);
	}

	@Override
	public ContObject getContObject() {
		return contObject;
	}

	public static MonitorContEventNotificationStatusV2 newInstance(
			ContObject contObject) {
		checkNotNull(contObject);
		MonitorContEventNotificationStatusV2 result = new MonitorContEventNotificationStatusV2(
				contObject);
		return result;
	}

	public ContEventLevelColorKeyV2 getContEventLevelColorKey() {
		return contEventLevelColorKey;
	}

	public void setContEventLevelColorKey(
			ContEventLevelColorKeyV2 contEventLevelColorKey) {
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
