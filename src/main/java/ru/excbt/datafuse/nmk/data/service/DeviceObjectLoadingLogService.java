package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectLoadingLog;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectLoadingLogRepository;

@Service
public class DeviceObjectLoadingLogService {

	@Autowired
	private DeviceObjectLoadingLogRepository deviceObjectLoadingLogRepository;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public DeviceObjectLoadingLog findOne(Long id) {
		return deviceObjectLoadingLogRepository.findOne(id);
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public DeviceObjectLoadingLog saveOne(DeviceObjectLoadingLog entity) {
		return deviceObjectLoadingLogRepository.save(entity);
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
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
