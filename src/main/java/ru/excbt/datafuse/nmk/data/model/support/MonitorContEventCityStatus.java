package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MonitorContEventCityStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1510890454161328379L;

	private final UUID cityFiasUUID;

	private final List<MonitorContEventNotificationStatus> contEventNotificationStatuses = new ArrayList<>();

	/**
	 * 
	 * @param cityFiasUUID
	 */
	public MonitorContEventCityStatus(UUID cityFiasUUID) {
		this.cityFiasUUID = cityFiasUUID;
	}

	/**
	 * 
	 * @return
	 */
	public String getCityName() {
		Optional<MonitorContEventNotificationStatus> item = contEventNotificationStatuses
				.stream()
				.filter((i) -> i.getContObject().getContObjectFias() != null
						&& i.getContObject().getContObjectFias()
								.getShortAddress2() != null).findFirst();
		return item.isPresent() ? item.get().getContObject()
				.getContObjectFias().getShortAddress2() : null;
	}

	/**
	 * 
	 * @return
	 */
	public UUID getCityFiasUUID() {
		return cityFiasUUID;
	}

	public List<MonitorContEventNotificationStatus> getContEventNotificationStatuses() {
		return contEventNotificationStatuses;
	}

	/**
	 * 
	 * @return
	 */
	public BigDecimal getCityGeoPosX() {
		Optional<MonitorContEventNotificationStatus> item = contEventNotificationStatuses
				.stream()
				.filter((i) -> i.getContObject().getContObjectGeo() != null
						&& i.getContObject().getContObjectGeo()
								.getCityGeoPosX() != null).findFirst();
		return item.isPresent() ? item.get().getContObject().getContObjectGeo()
				.getCityGeoPosX() : null;
	}

	/**
	 * 
	 * @return
	 */
	public BigDecimal getCityGeoPosY() {
		Optional<MonitorContEventNotificationStatus> item = contEventNotificationStatuses
				.stream()
				.filter((i) -> i.getContObject().getContObjectGeo() != null
						&& i.getContObject().getContObjectGeo()
								.getCityGeoPosY() != null).findFirst();
		return item.isPresent() ? item.get().getContObject().getContObjectGeo()
				.getCityGeoPosY() : null;
	}

}
