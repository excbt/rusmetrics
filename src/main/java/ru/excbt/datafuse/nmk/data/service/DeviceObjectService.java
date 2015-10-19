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
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectDataSourceRepository;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectMetaVzletRepository;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

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

	// public DeviceObject loadLazyDeviceObject(DeviceObject deviceObject) {
	// if (deviceObject != null && deviceObject.getContObject() != null) {
	// deviceObject.getContObject().getId();
	// deviceObject.getContObjectInfo().getName();
	// }
	// deviceObject.getActiveDataSource();
	// return deviceObject;
	// }

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
		DeviceObject deviceObject = findOne(deviceObjectId);
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
			;
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
	 * @param deviceObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	@Deprecated
	public DeviceObject createOne(DeviceObject deviceObject) {
		checkNotNull(deviceObject, "Argument DeviceObject is NULL");
		checkArgument(deviceObject.isNew());
		checkNotNull(deviceObject.getDeviceModelId(), "Device Model Id is NULL");
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
	@Deprecated
	public DeviceObject updateOne(DeviceObject deviceObject) {
		checkNotNull(deviceObject, "Argument DeviceObject is NULL");
		checkArgument(deviceObject.isNew());
		checkNotNull(deviceObject.getDeviceModelId(), "Device Model Id is NULL");
		checkNotNull(deviceObject.getContObject(), "ContObject is null");
		// deviceObject.setExSystemKeyname(ExSystemKey.MANUAL.getKeyname());
		// deviceObject.set
		DeviceObject result = deviceObjectRepository.save(deviceObject);
		result.loadLazyProps();
		return result;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public DeviceObject findOne(long id) {
		DeviceObject result = deviceObjectRepository.findOne(id);
		result.loadLazyProps();
		return result;
	}

	/**
	 * 
	 * @param deviceObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObject saveOne(DeviceObject deviceObject) {
		return saveOne(deviceObject, null);
	}

	/**
	 * 
	 * @param deviceObject
	 * @param deviceObjectDataSource
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObject saveOne(DeviceObject deviceObject, DeviceObjectDataSource deviceObjectDataSource) {
		// Checking
		checkNotNull(deviceObject, "Argument DeviceObject is NULL");
		checkNotNull(deviceObject.getDeviceModel(), "Device Model is NULL");
		if (deviceObjectDataSource != null) {
			checkArgument(deviceObjectDataSource.isNew());
			checkNotNull(deviceObjectDataSource.getSubscrDataSource());
		}
		// Set manual flag
		deviceObject.setIsManual(true);

		DeviceObject savedDeviceObject = deviceObjectRepository.save(deviceObject);
		if (deviceObjectDataSource != null) {
			deviceObjectDataSource.setDeviceObject(savedDeviceObject);
			deviceObjectDataSource.setDeviceObjectId(savedDeviceObject.getId());
			DeviceObjectDataSource resultDataSource = deviceObjectDataSourceService
					.saveDeviceDataSource(deviceObjectDataSource);
			savedDeviceObject.getDeviceObjectDataSources().add(resultDataSource);
		}

		DeviceObject result = findOne(savedDeviceObject.getId());
		return result;
	}

	/**
	 * 
	 * @param deviceObjectId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public void deleteOne(Long deviceObjectId) {
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
	public void deleteOnePermanent(Long deviceObjectId) {
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

}
