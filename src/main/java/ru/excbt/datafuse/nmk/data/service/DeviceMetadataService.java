package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.DeviceMetadata;
import ru.excbt.datafuse.nmk.data.repository.DeviceMetadataRepository;

@Service
public class DeviceMetadataService {

	private static final Logger logger = LoggerFactory.getLogger(DeviceMetadataService.class);

	public final static String DEVICE_METADATA_TYPE = "DEVICE";

	@Autowired
	private DeviceMetadataRepository deviceMetadataRepository;

	/**
	 * 
	 * @param deviceModelId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceMetadata> selectDeviceMetadata(Long deviceModelId, String deviceMeatdataType) {
		return deviceMetadataRepository.selectDeviceMetadata(deviceModelId, deviceMeatdataType);
	}
}
