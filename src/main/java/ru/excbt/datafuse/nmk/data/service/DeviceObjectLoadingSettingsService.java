package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectLoadingSettings;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectLoadingSettingsRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class DeviceObjectLoadingSettingsService implements SecuredRoles {

	@Autowired
	private DeviceObjectLoadingSettingsRepository deviceObjectLoadingSettingsRepository;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public DeviceObjectLoadingSettings findOne(Long id) {
		return deviceObjectLoadingSettingsRepository.findOne(id);
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public DeviceObjectLoadingSettings saveOne(DeviceObjectLoadingSettings entity) {
		checkNotNull(entity.getDeviceObject());
		checkArgument(!entity.getDeviceObject().isNew());
		if (!entity.isNew()) {
			checkArgument(entity.getDeviceObject().getId().equals(entity.getDeviceObjectId()));
		}
		entity.setDeviceObjectId(entity.getDeviceObject().getId());
		return deviceObjectLoadingSettingsRepository.save(entity);
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public DeviceObjectLoadingSettings getDeviceObjectLoadingSettings(DeviceObject entity) {
		if (entity.isNew()) {
			DeviceObjectLoadingSettings result = new DeviceObjectLoadingSettings();
			result.setDeviceObject(entity);
			result.setDeviceObjectId(entity.getId());
			return result;
		}

		List<DeviceObjectLoadingSettings> resultList = deviceObjectLoadingSettingsRepository
				.findByDeviceObjectId(entity.getId());
		if (resultList.isEmpty()) {
			DeviceObjectLoadingSettings result = new DeviceObjectLoadingSettings();
			result.setDeviceObject(entity);
			result.setDeviceObjectId(entity.getId());
			return result;
		}
		return resultList.get(0);
	}

	/**
	 * 
	 * @return
	 */
	public DeviceObjectLoadingSettings newDefaultDeviceObjectLoadingSettings(DeviceObject deviceObject) {
		checkNotNull(deviceObject);
		DeviceObjectLoadingSettings result = new DeviceObjectLoadingSettings();
		result.setDeviceObject(deviceObject);
		result.setDeviceObjectId(deviceObject.getId());
		result.setIsLoadingAuto(true);
		result.setLoadingAttempts(1);
		result.setLoadingInterval("01:00");
		return result;
	}

}
