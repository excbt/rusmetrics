package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectLoadingLog;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectLoadingLogRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

/**
 * Сервис для работы с протоколированием загрузки с прибора
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.01.2016
 *
 */
@Service
public class DeviceObjectLoadingLogService implements SecuredRoles {

	@Autowired
	private DeviceObjectLoadingLogRepository deviceObjectLoadingLogRepository;

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional( readOnly = true)
	public DeviceObjectLoadingLog findOne(Long id) {
		return deviceObjectLoadingLogRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(DeviceObjectLoadingLog.class, id));
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	@Transactional
	public DeviceObjectLoadingLog saveOne(DeviceObjectLoadingLog entity) {
		return deviceObjectLoadingLogRepository.save(entity);
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional( readOnly = true)
	public DeviceObjectLoadingLog getDeviceObjectLoadingLog(DeviceObject entity) {
		if (entity.isNew()) {
			DeviceObjectLoadingLog result = new DeviceObjectLoadingLog();
			result.setDeviceObject(entity);
			result.setDeviceObjectId(entity.getId());
			return result;
		}
		List<DeviceObjectLoadingLog> resultList = deviceObjectLoadingLogRepository.findByDeviceObjectId(entity.getId());
		if (resultList.isEmpty()) {
			DeviceObjectLoadingLog result = new DeviceObjectLoadingLog();
			result.setDeviceObject(entity);
			result.setDeviceObjectId(entity.getId());
			return result;
		}
		return resultList.get(0);
	}

}
