package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.DeviceMetadata;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectLoadingSettings;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;
import ru.excbt.datafuse.nmk.data.model.types.DataSourceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectDataSourceRepository;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectMetaVzletRepository;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

/**
 * Сервис для работы с приборами
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 28.05.2015
 *
 */
@Service
public class DeviceObjectService implements SecuredRoles {

	@Autowired
	private DeviceObjectRepository deviceObjectRepository;

	@Autowired
	private DeviceModelService deviceModelService;

	@Autowired
	private DeviceObjectMetaVzletRepository deviceObjectMetaVzletRepository;

	@Autowired
	private DeviceObjectDataSourceService deviceObjectDataSourceService;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	@Autowired
	private DeviceObjectDataSourceRepository deviceObjectDataSourceRepository;

	@Autowired
	private DeviceObjectMetadataService deviceObjectMetadataService;

	@Autowired
	private DeviceMetadataService deviceMetadataService;

	@Autowired
	private DeviceObjectLoadingSettingsService deviceObjectLoadingSettingsService;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObject createManualDeviceObject() {

		DeviceObject deviceObject = new DeviceObject();
		DeviceModel deviceModel = deviceModelService.findPortalDeviceModel();
		checkNotNull(deviceModel, "DeviceModel of Portal is not found");

		deviceObject.setDeviceModel(deviceModel);
		deviceObject.setExSystemKeyname(ExSystemKey.MANUAL.getKeyname());
		return deviceObjectRepository.save(deviceObject);
	}

	/**
	 * 
	 * @param deviceObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObject createManualDeviceObject(DeviceObject deviceObject) {
		checkNotNull(deviceObject, "Argument DeviceObject is NULL");
		checkArgument(deviceObject.isNew());
		checkNotNull(deviceObject.getDeviceModel(), "Device Model is NULL");
		deviceObject.setExSystemKeyname(ExSystemKey.MANUAL.getKeyname());
		return deviceObjectRepository.save(deviceObject);
	}

	/**
	 * 
	 * @param deviceObjectId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public void deleteManualDeviceObject(Long deviceObjectId) {
		DeviceObject deviceObject = findDeviceObject(deviceObjectId);
		if (ExSystemKey.MANUAL.isNotEquals(deviceObject.getExSystemKeyname())) {
			throw new PersistenceException(
					String.format("Delete DeviceObject(id=%d) with exSystem=%s is not supported ", deviceObjectId,
							deviceObject.getExSystemKeyname()));
		}
		deviceObjectRepository.delete(deviceObjectId);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceObject> selectDeviceObjectsByContObjectId(Long contObjectId) {
		List<DeviceObject> resultList = deviceObjectRepository.selectDeviceObjectsByContObjectId(contObjectId);
		resultList.forEach(i -> {
			i.loadLazyProps();
		});
		return resultList;
	}

	/**
	 * 
	 * @param deviceObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public DeviceObjectMetaVzlet selectDeviceObjectMetaVzlet(Long deviceObjectId) {
		List<DeviceObjectMetaVzlet> vList = deviceObjectMetaVzletRepository.findByDeviceObjectId(deviceObjectId);

		DeviceObjectMetaVzlet result = vList.size() > 0 ? vList.get(0) : null;
		if (result != null) {
			result.getVzletSystem1();
			result.getVzletSystem2();
			result.getVzletSystem3();
		}
		return result;
	}

	/**
	 * 
	 * @param deviceObjectMetaVzlet
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObjectMetaVzlet updateDeviceObjectMetaVzlet(DeviceObjectMetaVzlet deviceObjectMetaVzlet) {
		checkNotNull(deviceObjectMetaVzlet);
		if (deviceObjectMetaVzlet.getExcludeNulls() == null) {
			deviceObjectMetaVzlet.setExcludeNulls(false);
		}
		if (deviceObjectMetaVzlet.getMetaPropsOnly() == null) {
			deviceObjectMetaVzlet.setMetaPropsOnly(false);
		}
		return deviceObjectMetaVzletRepository.save(deviceObjectMetaVzlet);
	}

	/**
	 * 
	 * @param deviceObjectMetaVzlet
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public void deleteDeviceObjectMetaVzlet(Long deviceObjectId) {
		checkNotNull(deviceObjectId);

		DeviceObjectMetaVzlet entity = selectDeviceObjectMetaVzlet(deviceObjectId);
		if (entity != null) {
			deviceObjectMetaVzletRepository.delete(entity);
		}
	}

	/**
	 * 
	 * @param deviceObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceObjectMetaVzlet> findDeviceObjectMetaVzlet(long deviceObjectId) {
		return deviceObjectMetaVzletRepository.findByDeviceObjectId(deviceObjectId);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public DeviceObject findDeviceObject(long id) {
		DeviceObject result = deviceObjectRepository.findOne(id);
		if (result != null) {
			result.loadLazyProps();
		}
		return result;
	}

	/**
	 * 
	 * @param deviceObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObject saveDeviceObject(DeviceObject deviceObject) {
		return saveDeviceObject(deviceObject, null);
	}

	/**
	 * 
	 * @param deviceObject
	 * @param deviceObjectDataSource
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObject saveDeviceObject(DeviceObject deviceObject, DeviceObjectDataSource deviceObjectDataSource) {
		// Checking
		checkNotNull(deviceObject, "Argument DeviceObject is NULL");
		checkNotNull(deviceObject.getDeviceModel(), "Device Model is NULL");
		if (deviceObjectDataSource != null) {
			checkArgument(deviceObjectDataSource.isNew());
			checkNotNull(deviceObjectDataSource.getSubscrDataSource());
		}
		// Set manual flag
		deviceObject.setIsManual(true);

		DeviceObject oldDeviceObject = deviceObject.isNew() ? null
				: deviceObjectRepository.findOne(deviceObject.getId());

		boolean isNew = deviceObject.isNew();

		DeviceObject savedDeviceObject = deviceObjectRepository.save(deviceObject);
		if (deviceObjectDataSource != null) {
			deviceObjectDataSource.setDeviceObject(savedDeviceObject);
			deviceObjectDataSource.setDeviceObjectId(savedDeviceObject.getId());
			DeviceObjectDataSource resultDataSource = deviceObjectDataSourceService
					.saveDeviceDataSource(deviceObjectDataSource);
			savedDeviceObject.getDeviceObjectDataSources().add(resultDataSource);

			if (ExSystemKey.VZLET.getKeyname()
					.equals(resultDataSource.getSubscrDataSource().getDataSourceType().getKeyname())) {

			}

		}

		// Проверяем источник данных для прямой загрузки 
		if (deviceObjectDataSource != null && deviceObjectDataSource.getSubscrDataSource() != null
				&& DataSourceTypeKey.DEVICE
						.equalsKeyname(deviceObjectDataSource.getSubscrDataSource().getDataSourceTypeKey())) {

			if (oldDeviceObject == null
					|| !oldDeviceObject.getDeviceModelId().equals(deviceObject.getDeviceModelId())) {
				List<DeviceMetadata> deviceMetadataList = deviceMetadataService.selectDeviceMetadata(
						deviceObject.getDeviceModelId(), DeviceMetadataService.DEVICE_METADATA_TYPE);
				// Если изменилась модель, а новых типовых метаданных нет, то бросаем exception
				if (deviceMetadataList.size() == 0) {
					throw new PersistenceException(String.format("DeviceMetadata for DeviceModel (id=%d) is not found",
							deviceObject.getDeviceModelId()));
				}

				// Удаляем текущие метаданные
				deviceObjectMetadataService.deleteDeviceObjectMetadata(deviceObject.getId());
				// Заполняем типовые метаданные
				deviceObjectMetadataService.copyDeviceMetadata(deviceObject.getDeviceModelId(), deviceObject.getId());
			}

		}

		if (deviceObjectDataSource != null && deviceObjectDataSource.getSubscrDataSource() != null
				&& DataSourceTypeKey.VZLET
						.equalsKeyname(deviceObjectDataSource.getSubscrDataSource().getDataSourceTypeKey())) {
			deviceObjectMetaVzletInit(savedDeviceObject);
		}

		// DeviceObjectLoadingSettings create new
		if (isNew) {
			DeviceObjectLoadingSettings deviceObjectLoadingSettings = deviceObjectLoadingSettingsService
					.newDefaultDeviceObjectLoadingSettings(savedDeviceObject);
			deviceObjectLoadingSettingsService.saveOne(deviceObjectLoadingSettings);
		} else {
			DeviceObjectLoadingSettings deviceObjectLoadingSettings = deviceObjectLoadingSettingsService
					.getDeviceObjectLoadingSettings(savedDeviceObject);
			if (deviceObjectLoadingSettings.isNew()) {
				deviceObjectLoadingSettings = deviceObjectLoadingSettingsService
						.newDefaultDeviceObjectLoadingSettings(savedDeviceObject);
				deviceObjectLoadingSettingsService.saveOne(deviceObjectLoadingSettings);
			}
		}

		DeviceObject result = findDeviceObject(savedDeviceObject.getId());

		return result;
	}

	/**
	 * 
	 * @param deviceObjectId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public void deleteDeviceObject(Long deviceObjectId) {
		checkNotNull(deviceObjectId);
		DeviceObject deviceObject = deviceObjectRepository.findOne(deviceObjectId);
		if (deviceObject == null) {
			throw new PersistenceException(String.format("DeviceObject (id=%d) is not found", deviceObjectId));
		}
		deviceObject.setDeleted(1);
		deviceObjectRepository.save(deviceObject);
	}

	/**
	 * 
	 * @param deviceObjectId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public void deleteDeviceObjectPermanent(Long deviceObjectId) {
		checkNotNull(deviceObjectId);
		DeviceObject deviceObject = deviceObjectRepository.findOne(deviceObjectId);
		if (deviceObject == null) {
			throw new PersistenceException(String.format("DeviceObject (id=%d) is not found", deviceObjectId));
		}

		deviceObject.getDeviceObjectDataSources().forEach(i -> {
			deviceObjectDataSourceRepository.delete(i);
		});

		deviceObjectRepository.delete(deviceObject);
	}

	/**
	 * 
	 * @param subscriderId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceObject> selectDeviceObjectsBySubscriber(Long subscriberId) {
		List<DeviceObject> result = subscrContObjectService.selectDeviceObjects(subscriberId);
		result.forEach(i -> {
			i.loadLazyProps();
		});
		return result;
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObjectMetaVzlet deviceObjectMetaVzletInit(DeviceObject entity) {
		checkNotNull(entity);
		List<DeviceObjectMetaVzlet> metaVzletList = deviceObjectMetaVzletRepository
				.findByDeviceObjectId(entity.getId());
		DeviceObjectMetaVzlet result;
		if (metaVzletList.isEmpty()) {
			result = new DeviceObjectMetaVzlet();
			result.setDeviceObject(entity);
			result = updateDeviceObjectMetaVzlet(result);
		} else {
			result = metaVzletList.get(0);
		}

		return result;
	}

}
