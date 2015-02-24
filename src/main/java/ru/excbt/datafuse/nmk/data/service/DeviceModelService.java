package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.repository.DeviceModelRepository;

@Service
@Transactional
public class DeviceModelService {

	@Autowired
	private DeviceModelRepository deviceModelRepository;

	@Transactional
	public DeviceModel save(DeviceModel entity) {
		return deviceModelRepository.save(entity);
	}

	@Transactional
	public void delete(DeviceModel entity) {
		deviceModelRepository.delete(entity);
	}

	@Transactional
	public void delete(Long id) {
		deviceModelRepository.delete(id);
	}
	
	
}
