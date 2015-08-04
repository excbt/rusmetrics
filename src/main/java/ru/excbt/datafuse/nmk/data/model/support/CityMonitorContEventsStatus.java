package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "cityFiasUUID", "cityName", "cityGeoPosX", "cityGeoPosY",
		"monitorEventCount" })
public class CityMonitorContEventsStatus extends
		CityContObjects<MonitorContEventNotificationStatus> implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1510890454161328379L;

	public static final CityContObjectsServiceTypeInfoFactory FACTORY_INSTANCE = new CityContObjectsServiceTypeInfoFactory();

	/**
	 * 
	 * @author kovtonyk
	 *
	 */
	public static class CityContObjectsServiceTypeInfoFactory implements
			CityContObjectsFactory<CityMonitorContEventsStatus> {

		@Override
		public CityMonitorContEventsStatus newInstance(UUID uuid) {
			return new CityMonitorContEventsStatus(uuid);
		}

	}

	private Long monitorEventCount = 0L;

	/**
	 * 
	 * @param cityFiasUUID
	 */
	public CityMonitorContEventsStatus(UUID cityFiasUUID) {
		super(cityFiasUUID);
	}

	public List<MonitorContEventNotificationStatus> getContEventNotificationStatuses() {
		return cityObjects;
	}

	public Long getMonitorEventCount() {
		return monitorEventCount;
	}

	public void setMonitorEventCount(Long monitorEventCount) {
		this.monitorEventCount = monitorEventCount;
	}

}
