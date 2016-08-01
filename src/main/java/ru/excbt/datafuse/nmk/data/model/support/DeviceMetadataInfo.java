package ru.excbt.datafuse.nmk.data.model.support;

import java.util.List;

public class DeviceMetadataInfo extends EntityColumn {

	private final String deviceMapping;
	private final String deviceMappingInfo;

	/**
	 * 
	 * @param columnName
	 * @param dataType
	 * @param deviceMappingList
	 */
	public DeviceMetadataInfo(String columnName, String dataType, List<String> deviceMappingList) {
		super(columnName, dataType);
		this.deviceMapping = deviceMappingList != null && deviceMappingList.size() >= 1 ? deviceMappingList.get(0)
				: null;
		this.deviceMappingInfo = deviceMappingList != null && deviceMappingList.size() >= 1 ? deviceMappingList.get(1)
				: null;
	}

	/**
	 * 
	 * @return
	 */
	public String getDeviceMapping() {
		return deviceMapping;
	}

	/**
	 * 
	 * @return
	 */
	public String getDeviceMappingInfo() {
		return deviceMappingInfo;
	}

}
