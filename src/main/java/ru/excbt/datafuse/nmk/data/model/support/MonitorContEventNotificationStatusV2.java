package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.markers.StatusColorRankObject;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;

public class MonitorContEventNotificationStatusV2 implements Serializable,
		StatusColorRankObject, ContObjectHolder {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5870004714222214147L;

	private final ContObject contObject;

	private ContEventLevelColorKeyV2 contEventLevelColorKey;

	private long eventsCount;

	private long eventsTypesCount;

	private long newEventsCount;

	/**
	 * 
	 * @param contObject
	 */
	private MonitorContEventNotificationStatusV2(ContObject contObject) {
		this.contObject = ContObjectShort.newInstance(contObject);
	}

	/**
	 * 
	 * @param contObject
	 * @return
	 */
	public static MonitorContEventNotificationStatusV2 newInstance(
			ContObject contObject) {
		checkNotNull(contObject);
		MonitorContEventNotificationStatusV2 result = new MonitorContEventNotificationStatusV2(
				contObject);
		return result;
	}

	/**
	 * 
	 */
	@Override
	public ContObject getContObject() {
		return contObject;
	}

	/**
	 * 
	 * @return
	 */
	public ContEventLevelColorKeyV2 getContEventLevelColorKey() {
		return contEventLevelColorKey;
	}

	/**
	 * 
	 * @param contEventLevelColorKey
	 */
	public void setContEventLevelColorKey(
			ContEventLevelColorKeyV2 contEventLevelColorKey) {
		this.contEventLevelColorKey = contEventLevelColorKey;
	}

	/**
	 * 
	 */
	@Override
	public String getStatusColor() {
		return contEventLevelColorKey == null ? null : contEventLevelColorKey
				.getKeyname();
	}

	/**
	 * 
	 * @return
	 */
	public long getEventsCount() {
		return eventsCount;
	}

	/**
	 * 
	 * @param eventsCount
	 */
	public void setEventsCount(long eventsCount) {
		this.eventsCount = eventsCount;
	}

	/**
	 * 
	 * @return
	 */
	public long getEventsTypesCount() {
		return eventsTypesCount;
	}

	/**
	 * 
	 * @param eventsTypesCount
	 */
	public void setEventsTypesCount(long eventsTypesCount) {
		this.eventsTypesCount = eventsTypesCount;
	}

	/**
	 * 
	 * @return
	 */
	public long getNewEventsCount() {
		return newEventsCount;
	}

	/**
	 * 
	 * @param newEventsCount
	 */
	public void setNewEventsCount(long newEventsCount) {
		this.newEventsCount = newEventsCount;
	}

	/* (non-Javadoc)
	 * @see ru.excbt.datafuse.nmk.data.model.markers.StatusColorObject#getColorRank()
	 */
	@Override
	public int getColorRank() {
		return contEventLevelColorKey == null ? Integer.MIN_VALUE : contEventLevelColorKey
				.getColorRank();
	}

}
