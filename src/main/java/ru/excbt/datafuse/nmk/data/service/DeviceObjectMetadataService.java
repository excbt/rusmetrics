package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetadata;
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
	public List<MeasureUnit> selectMeasureUnits() {

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

	/**
	 * 
	 * @param deviceObjectMetadataId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public DeviceObjectMetadata findOne(Long deviceObjectMetadataId) {
		return deviceObjectMetadataRepository.findOne(deviceObjectMetadataId);
	}

	/**
	 * 
	 * @param deviceObjectMetadataList
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<DeviceObjectMetadata> updateDeviceObjectMetadata(Long deviceObjectId,
			List<DeviceObjectMetadata> deviceObjectMetadataList) {
		checkNotNull(deviceObjectMetadataList);

		List<DeviceObjectMetadata> checkMetadata = selectDeviceObjectMetadata(deviceObjectId);
		deviceObjectMetadataList.forEach(i -> {
			checkArgument(!i.isNew());

			if (checkMetadata.stream()
					.anyMatch(chk -> chk.getId().equals(i.getId())
							&& chk.getDeviceObjectId().equals(i.getDeviceObjectId())
							&& chk.getDeleted() == i.getDeleted())) {
				return;
			}
			throw new PersistenceException(
					String.format("Modifying metadata for deviceObjectId=%d is not possible", deviceObjectId));
		});

		return Lists.newArrayList(deviceObjectMetadataRepository.save(deviceObjectMetadataList));

	}

}
