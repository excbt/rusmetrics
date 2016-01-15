package ru.excbt.datafuse.nmk.data.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.repository.DeviceModelRepository;

@Service
public class DeviceModelService {

	@Autowired
	private DeviceModelRepository deviceModelRepository;

	////////////
	public static final Comparator<DeviceModel> COMPARE_BY_NAME = (a, b) -> {
		if (a == b) {
			return 0;
		}
		if (a == null || a.getModelName() == null) {
			return -1;
		}
		if (b == null || b.getModelName() == null) {
			return 1;
		}

		return a.getModelName().compareTo(b.getModelName());
	};

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

		List<DeviceModel> result = deviceModelRepository.findByExSystem(exSystem);

		return ObjectFilters.deletedFilter(result);
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public DeviceModel findPortalDeviceModel() {

		List<DeviceModel> pre = findDeviceModelsByExSystem(ExSystemKey.PORTAL.getKeyname());

		Optional<DeviceModel> result = pre.stream().sorted((m1, m2) -> Long.compare(m1.getId(), m2.getId()))
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

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceModel> findAll() {
		List<DeviceModel> preResult = Lists.newArrayList(deviceModelRepository.findAll());
		return ObjectFilters.deletedFilter(preResult);
	}

}
