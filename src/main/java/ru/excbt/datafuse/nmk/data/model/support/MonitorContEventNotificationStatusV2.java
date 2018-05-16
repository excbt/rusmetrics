package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.markers.StatusColorRankObject;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.model.v.ContObjectGeoPos;
import ru.excbt.datafuse.nmk.data.model.vo.ContObjectVOFias;

public class MonitorContEventNotificationStatusV2
		implements Serializable, StatusColorRankObject, ContObjectHolder, ContObjectFiasHolder, ContObjectGeoPosHolder {

	/**
	 *
	 */
	private static final long serialVersionUID = -5870004714222214147L;

	private final ContObjectVOFias contObjectVOFias;

	private ContEventLevelColorKeyV2 contEventLevelColorKey;

	private long eventsCount;

	private long eventsTypesCount;

	private long newEventsCount;

	/**
	 *
	 * @param contObject
	 */
	private MonitorContEventNotificationStatusV2(ContObjectDTO contObject, ContObjectFias contObjectFias,
                                                 ContObjectGeoPos contObjectGeoPos) {
		this.contObjectVOFias = new ContObjectVOFias(contObject, contObjectFias,
				contObjectGeoPos);
	}

	/**
	 *
	 * @param contObject
	 * @return
	 */
	public static MonitorContEventNotificationStatusV2 newInstance(ContObjectDTO contObject, ContObjectFias contObjectFias,
			ContObjectGeoPos contObjectGeoPos) {
		checkNotNull(contObject);
		MonitorContEventNotificationStatusV2 result = new MonitorContEventNotificationStatusV2(contObject,
				contObjectFias, contObjectGeoPos);
		return result;
	}

	/**
	 *
	 */
	@Override
	public ContObjectDTO getContObject() {
		return contObjectVOFias.getModel();
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
	public void setContEventLevelColorKey(ContEventLevelColorKeyV2 contEventLevelColorKey) {
		this.contEventLevelColorKey = contEventLevelColorKey;
	}

	/**
	 *
	 */
	@Override
	public String getStatusColor() {
		return contEventLevelColorKey == null ? null : contEventLevelColorKey.getKeyname();
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
		return contEventLevelColorKey == null ? Integer.MIN_VALUE : contEventLevelColorKey.getColorRank();
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
