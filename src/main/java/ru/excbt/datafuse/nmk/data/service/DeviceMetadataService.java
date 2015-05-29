package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.DeviceMetadata;
import ru.excbt.datafuse.nmk.data.repository.DeviceMetadataRepository;

@Service
@Transactional
public class DeviceMetadataService {

	private static final Logger logger = LoggerFactory
			.getLogger(DeviceMetadataService.class);

	@Autowired
	private DeviceMetadataRepository deviceMetadataRepository;

	/**
	 * 
	 * @param deviceModelId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<DeviceMetadata> findDeviceMetadata(Long deviceModelId) {
		return deviceMetadataRepository.findByDeviceModelIdOrderByMetaOrderAsc(deviceModelId);
	}
}
