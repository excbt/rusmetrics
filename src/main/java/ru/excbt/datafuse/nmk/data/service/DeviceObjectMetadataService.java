package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetadata;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.keyname.MeasureUnit;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectMetadataRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.MeasureUnitRepository;

/**
 * Сервис для работы с метаданными прибора
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.01.2016
 *
 */
@Service
public class DeviceObjectMetadataService {

	private final static String DEVICE_METADATA_TYPE = "DEVICE";

	@Autowired
	private MeasureUnitRepository measureUnitRepository;

	@Autowired
	private DeviceObjectMetadataRepository deviceObjectMetadataRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<MeasureUnit> getMeasureUnits() {

		List<MeasureUnit> resultList = measureUnitRepository.findAll();

		return ObjectFilters.deletedFilter(resultList);

	}

	/**
	 * 
	 * @param deviceObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceObjectMetadata> selectDeviceObjectMetadata(Long deviceObjectId) {
		List<DeviceObjectMetadata> result = deviceObjectMetadataRepository.selectDeviceObjectMetadata(deviceObjectId,
				DEVICE_METADATA_TYPE);
		return ObjectFilters.deletedFilter(result);
	}

}
