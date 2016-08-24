package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;

@JsonPropertyOrder({ "cityFiasUUID", "cityName", "cityGeoPosX", "cityGeoPosY",
		"monitorEventCount", "cityContEventLevelColor" })
public class CityMonitorContEventsStatusV2 extends
		CityContObjects<MonitorContEventNotificationStatusV2> implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1510890454161328379L;

	public static final CityContObjectsFactory<CityMonitorContEventsStatusV2> FACTORY_INSTANCE = new CityContObjectsServiceTypeInfoFactory();

	/**
	 * 
	 * @author kovtonyk
	 *
	 */
	static class CityContObjectsServiceTypeInfoFactory implements
			CityContObjectsFactory<CityMonitorContEventsStatusV2> {

		@Override
		public CityMonitorContEventsStatusV2 newInstance(UUID uuid) {
			return new CityMonitorContEventsStatusV2(uuid);
		}

	}

	private Long monitorEventCount = 0L;

	/**
	 * 
	 * @param cityFiasUUID
	 */
	public CityMonitorContEventsStatusV2(UUID cityFiasUUID) {
		super(cityFiasUUID);
	}

	/**
	 * 
	 * @return
	 */
	public List<MonitorContEventNotificationStatusV2> getContEventNotificationStatuses() {
		return cityObjects;
	}

	/**
	 * 
	 * @return
	 */
	public Long getMonitorEventCount() {
		return monitorEventCount;
	}

	/**
	 * 
	 * @param monitorEventCount
	 */
	public void setMonitorEventCount(Long monitorEventCount) {
		this.monitorEventCount = monitorEventCount;
	}

	/**
	 * 
	 * @return
	 */
	public ContEventLevelColorKeyV2 getCityContEventLevelColor() {

		Optional<MonitorContEventNotificationStatusV2> item = cityObjects
				.stream()
				.filter((i) -> i.getContEventLevelColorKey() != null)
				.sorted((x, y) -> Integer.compare(x.getContEventLevelColorKey()
						.getColorRank(), y.getContEventLevelColorKey()
								.getColorRank()))
				.findFirst();

		return item.isPresent() ? item.get().getContEventLevelColorKey() : null;
	}

}
