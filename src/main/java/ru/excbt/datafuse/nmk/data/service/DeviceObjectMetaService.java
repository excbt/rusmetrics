package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectMetaVzletRepository;

@Service
@Transactional
public class DeviceObjectMetaService {

	@Autowired
	private DeviceObjectMetaVzletRepository metaVzletRepository;
	

	/**
	 * 
	 * @param deviceObjectId
	 * @return
	 */
	@Transactional (readOnly = true)
	public List<DeviceObjectMetaVzlet> findMetaVzlet(long deviceObjectId) {
		return metaVzletRepository.findByDeviceObjectId(deviceObjectId);
	}
	
	
}
