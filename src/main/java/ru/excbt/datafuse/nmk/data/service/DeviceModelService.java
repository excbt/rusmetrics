package ru.excbt.datafuse.nmk.data.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.repository.DeviceModelRepository;

@Service
public class DeviceModelService {

	@Autowired
	private DeviceModelRepository deviceModelRepository;

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public DeviceModel save(DeviceModel entity) {
		return deviceModelRepository.save(entity);
	}

	/**
	 * 
	 * @param entity
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void delete(DeviceModel entity) {
		deviceModelRepository.delete(entity);
	}

	/**
	 * 
	 * @param id
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void delete(Long id) {
		deviceModelRepository.delete(id);
	}

	/**
	 * 
	 * @param exSystem
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceModel> findDeviceModelsByExSystem(String exSystem) {

		List<DeviceModel> result = deviceModelRepository
				.findByExSystem(exSystem);

		return result;
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public DeviceModel findOne(Long id) {
		return deviceModelRepository.findOne(id);
	}

}
