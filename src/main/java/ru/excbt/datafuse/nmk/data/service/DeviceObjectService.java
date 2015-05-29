package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectRepository;

@Service
@Transactional
public class DeviceObjectService {

	@Autowired
	private DeviceObjectRepository deviceObjectRepository;

	/**
	 * 
	 * @param id
	 * @return
	 */
	public DeviceObject findOne(long id) {
		return deviceObjectRepository.findOne(id);
	}
}
