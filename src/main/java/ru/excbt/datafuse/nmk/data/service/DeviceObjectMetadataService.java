package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.DeviceMetadata;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetadata;
import ru.excbt.datafuse.nmk.data.model.keyname.MeasureUnit;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectMetadataRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.MeasureUnitRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

/**
 * Сервис для работы с метаданными прибора
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.01.2016
 *
 */
@Service
public class DeviceObjectMetadataService implements SecuredRoles {

	public final static String DEVICE_METADATA_TYPE = DeviceMetadataService.DEVICE_METADATA_TYPE;

	@Autowired
	private MeasureUnitRepository measureUnitRepository;

	@Autowired
	private DeviceObjectMetadataRepository deviceObjectMetadataRepository;

	@Autowired
	private DeviceMetadataService deviceMetadataService;

	@Autowired
	private DeviceObjectService deviceObjectService;

	@Autowired
	private ContZPointService contZPointService;

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
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<MeasureUnit> selectMeasureUnitsSame(String measureUnit) {

		List<MeasureUnit> resultList = measureUnitRepository.selectMeasureUnitsSame(measureUnit);

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
		//result.sort((a, b) -> a.getId().compareTo(b.getId()));
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
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
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

	/**
	 * 
	 * @param srcDeviceObjectId
	 * @param destDeviceObjectId
	 * @return
	 */
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<DeviceObjectMetadata> copyDeviceObjectMetadata(Long srcDeviceObjectId, Long destDeviceObjectId) {
		List<DeviceObjectMetadata> srcMetadata = selectDeviceObjectMetadata(srcDeviceObjectId);
		List<DeviceObjectMetadata> newMetadata = new ArrayList<>();
		srcMetadata.forEach(src -> {
			DeviceObjectMetadata dst = new DeviceObjectMetadata();
			dst.setDeviceMetadataType(src.getDeviceMetadataType());
			dst.setDeviceObjectId(destDeviceObjectId);
			dst.setContServiceType(src.getContServiceType());
			dst.setSrcProp(src.getSrcProp());
			dst.setDestProp(src.getDestProp());
			dst.setIsIntegrator(src.getIsIntegrator());
			dst.setSrcPropDivision(src.getSrcPropDivision());
			dst.setDestPropCapacity(src.getDestPropCapacity());
			dst.setSrcMeasureUnit(src.getSrcMeasureUnit());
			dst.setDestMeasureUnit(src.getDestMeasureUnit());
			dst.setMetaNumber(src.getMetaNumber());
			dst.setMetaOrder(src.getMetaOrder());
			//dst.setMetaDescription(src.getMetaDescription());
			//dst.setMetaComment(src.getMetaComment());
			dst.setPropVars(src.getPropVars());
			dst.setPropFunc(src.getPropFunc());
			dst.setDestDbType(src.getDestDbType());
			dst.setMetaVersion(src.getMetaVersion());
			newMetadata.add(dst);
		});
		return Lists.newArrayList(deviceObjectMetadataRepository.save(newMetadata));
	}

	/**
	 * 
	 * @param deviceObjectId
	 * @return
	 */
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteDeviceObjectMetadata(Long deviceObjectId) {
		List<DeviceObjectMetadata> metadata = selectDeviceObjectMetadata(deviceObjectId);
		deviceObjectMetadataRepository.delete(metadata);
	}

	/**
	 * 
	 * @param srcDeviceModelId
	 * @param destDeviceObjectId
	 * @return
	 */
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<DeviceObjectMetadata> copyDeviceMetadata(Long deviceModelId, Long destDeviceObjectId) {

		checkNotNull(deviceModelId, "deviceModelId is null");

		DeviceObject deviceObject = deviceObjectService.findDeviceObject(destDeviceObjectId);
		if (deviceObject == null) {
			throw new PersistenceException(String.format("DeviceObject (id=%d) is not found", destDeviceObjectId));
		}

		if (!deviceModelId.equals(deviceObject.getDeviceModelId())) {
			throw new PersistenceException(String.format(
					"DeviceObject (id=%d) has different model. "
							+ "Actual DeviceModel (id=%d), Expected DeviceModel(id=%d)",
					destDeviceObjectId, deviceObject.getDeviceModelId(), deviceModelId));
		}

		List<DeviceObjectMetadata> newMetadata = new ArrayList<>();
		List<DeviceMetadata> srcDeviceMetadata = deviceMetadataService.selectDeviceMetadata(deviceModelId,
				DEVICE_METADATA_TYPE);

		if (srcDeviceMetadata.isEmpty()) {
			throw new PersistenceException(
					String.format("DeviceMetadata for DeviceModel (id=%d) is not found", deviceModelId));
		}

		srcDeviceMetadata.forEach(src -> {
			DeviceObjectMetadata dst = new DeviceObjectMetadata();

			dst.setDeviceMetadataType(src.getDeviceMetadataType());
			dst.setDeviceObjectId(destDeviceObjectId);
			dst.setSrcProp(src.getSrcProp());
			dst.setDestProp(src.getDestProp());
			dst.setIsIntegrator(src.getIsIntegrator());
			dst.setSrcPropDivision(src.getSrcPropDivision());
			dst.setDestPropCapacity(src.getDestPropCapacity());
			dst.setSrcMeasureUnit(src.getSrcMeasureUnit());
			dst.setDestMeasureUnit(src.getDestMeasureUnit());
			dst.setMetaNumber(src.getMetaNumber());
			dst.setMetaOrder(src.getMetaOrder());
			dst.setMetaDescription(src.getMetaDescription());
			dst.setMetaComment(src.getMetaComment());
			dst.setPropVars(src.getPropVars());
			dst.setPropFunc(src.getPropFunc());
			dst.setDestDbType(src.getDestDbType());
			dst.setMetaVersion(src.getMetaVersion());

			newMetadata.add(dst);
		});

		return Lists.newArrayList(deviceObjectMetadataRepository.save(newMetadata));
	}

	/**
	 * 
	 * @param contZPointId
	 * @param deviceMetadataType
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceObjectMetadata> selectByContZPoint(Long contZPointId, String deviceMetadataType) {
		List<DeviceObjectMetadata> result = new ArrayList<>();
		List<DeviceObject> deviceObjects = contZPointService.selectDeviceObjects(contZPointId);
		if (deviceObjects.isEmpty() || deviceObjects.size() > 1) {
			return result;
		}

		DeviceObject deviceObject = deviceObjects.get(0);

		result = deviceObjectMetadataRepository.selectDeviceObjectMetadata(deviceObject.getId(), deviceMetadataType);

		return result;
	}

}
