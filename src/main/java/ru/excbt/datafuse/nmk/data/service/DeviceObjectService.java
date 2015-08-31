package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectMetaVzletRepository;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class DeviceObjectService implements SecuredRoles {

	@Autowired
	private DeviceObjectRepository deviceObjectRepository;

	@Autowired
	private DeviceModelService deviceModelService;

	@Autowired
	private DeviceObjectMetaVzletRepository deviceObjectMetaVzletRepository;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public DeviceObject findOne(long id) {
		return deviceObjectRepository.findOne(id);
	}

	@Secured({ ROLE_DEVICE_OBJECT_ADMIN })
	public DeviceObject saveOne(DeviceObject deviceObject) {
		return deviceObjectRepository.save(deviceObject);
	}

	@Secured({ ROLE_DEVICE_OBJECT_ADMIN })
	public DeviceObject createManualDeviceObject() {

		DeviceObject deviceObject = new DeviceObject();
		DeviceModel deviceModel = deviceModelService.findPortalDeviceModel();
		checkNotNull(deviceModel, "DeviceModel of Portal is not found");

		deviceObject.setDeviceModel(deviceModel);
		deviceObject.setExSystemKeyname(ExSystemKey.MANUAL.getKeyname());
		return deviceObjectRepository.save(deviceObject);
	}

	@Secured({ ROLE_DEVICE_OBJECT_ADMIN })
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
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN })
	public void deleteManualDeviceObject(Long deviceObjectId) {
		DeviceObject deviceObject = findOne(deviceObjectId);
		if (ExSystemKey.MANUAL.isNotEquals(deviceObject.getExSystemKeyname())) {
			throw new PersistenceException(
					String.format(
							"Delete DeviceObject(id=%d) with exSystem=%s is not supported ",
							deviceObjectId, deviceObject.getExSystemKeyname()));
		}
		deviceObjectRepository.delete(deviceObjectId);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<DeviceObject> selectDeviceObjectsByContObjectId(
			Long contObjectId) {
		return deviceObjectRepository
				.selectDeviceObjectsByContObjectId(contObjectId);
	}

	/**
	 * 
	 * @param deviceObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public DeviceObjectMetaVzlet selectDeviceObjectMetaVzlet(Long deviceObjectId) {
		List<DeviceObjectMetaVzlet> vList = deviceObjectMetaVzletRepository
				.findByDeviceObjectId(deviceObjectId);

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
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN })
	public DeviceObjectMetaVzlet updateDeviceObjectMetaVzlet(
			DeviceObjectMetaVzlet deviceObjectMetaVzlet) {
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
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN })
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
	@Transactional(readOnly = true)
	public List<DeviceObjectMetaVzlet> findDeviceObjectMetaVzlet(
			long deviceObjectId) {
		return deviceObjectMetaVzletRepository
				.findByDeviceObjectId(deviceObjectId);
	}

}
