package ru.excbt.datafuse.nmk.data.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.repository.DeviceModelRepository;

@Service
@Transactional
public class DeviceModelService {

	@Autowired
	private DeviceModelRepository deviceModelRepository;

	public DeviceModel save(DeviceModel entity) {
		return deviceModelRepository.save(entity);
	}

	public void delete(DeviceModel entity) {
		deviceModelRepository.delete(entity);
	}

	public void delete(Long id) {
		deviceModelRepository.delete(id);
	}

	/**
	 * 
	 * @param exSystem
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<DeviceModel> findDeviceModelsByExSystem(String exSystem) {

		List<DeviceModel> result = deviceModelRepository
				.findByExSystem(exSystem);

		return result;
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public DeviceModel findPortalDeviceModel() {

		List<DeviceModel> pre = findDeviceModelsByExSystem(ExSystemKey.PORTAL
				.getKeyname());

		Optional<DeviceModel> result = pre.stream()
				.sorted((m1, m2) -> Long.compare(m1.getId(), m2.getId()))
				.findFirst();

		return result.isPresent() ? result.get() : null;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public DeviceModel findOne(Long id) {
		return deviceModelRepository.findOne(id);
	}

}
