package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.markers.StatusColorObject;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.model.v.ContObjectGeoPos;
import ru.excbt.datafuse.nmk.data.model.vo.ContObjectVOFias;

@Deprecated
public class MonitorContEventNotificationStatus
		implements Serializable, StatusColorObject, ContObjectHolder, ContObjectFiasHolder, ContObjectGeoPosHolder {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5870004714222214147L;

	private final ContObjectVOFias contObjectVOFias;

	private ContEventLevelColorKey contEventLevelColorKey;

	private long eventsCount;

	private long eventsTypesCount;

	private long newEventsCount;

	public MonitorContEventNotificationStatus(ContObject contObject, ContObjectFias contObjectFias,
			ContObjectGeoPos contObjectGeoPos) {
		this.contObjectVOFias = new ContObjectVOFias(ContObjectShort.newInstance(contObject), contObjectFias,
				contObjectGeoPos);
	}

	@Override
	public ContObject getContObject() {
		return contObjectVOFias.getModel();
	}

	public static MonitorContEventNotificationStatus newInstance(ContObject contObject, ContObjectFias contObjectFias,
			ContObjectGeoPos contObjectGeoPos) {
		checkNotNull(contObject);
		MonitorContEventNotificationStatus result = new MonitorContEventNotificationStatus(contObject, contObjectFias,
				contObjectGeoPos);
		return result;
	}

	public ContEventLevelColorKey getContEventLevelColorKey() {
		return contEventLevelColorKey;
	}

	public void setContEventLevelColorKey(ContEventLevelColorKey contEventLevelColorKey) {
		this.contEventLevelColorKey = contEventLevelColorKey;
	}

	@Override
	public String getStatusColor() {
		return contEventLevelColorKey == null ? null : contEventLevelColorKey.getKeyname();
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

	/* (non-Javadoc)
	 * @see ru.excbt.datafuse.nmk.data.model.support.ContObjectFiasHolder#getContObjectFias()
	 */
	@Override
	public ContObjectFias getContObjectFias() {
		return contObjectVOFias.getContObjectFias();
	}

	/* (non-Javadoc)
	 * @see ru.excbt.datafuse.nmk.data.model.support.ContObjectGeoPosHolder#getContObjectGeoPos()
	 */
	@Override
	public ContObjectGeoPos getContObjectGeo() {
		return contObjectVOFias.getContObjectGeo();
	}

}
