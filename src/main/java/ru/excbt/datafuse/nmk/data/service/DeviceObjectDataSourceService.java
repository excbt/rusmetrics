package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectDataSourceRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

/**
 * Сервис для работы с источником данных для прибора
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.10.2015
 *
 */
@Service
public class DeviceObjectDataSourceService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(DeviceObjectDataSourceService.class);

	@Autowired
	private DeviceObjectDataSourceRepository deviceObjectDataSourceRepository;

	/**
	 * 
	 * @param deviceObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceObjectDataSource> selectActiveDeviceObjectDataSource(Long deviceObjectId) {
		List<DeviceObjectDataSource> resultList = deviceObjectDataSourceRepository
				.selectActiveDataSource(deviceObjectId);
		return resultList;
	}

	/**
	 * 
	 * @param deviceObjectDataSource
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObjectDataSource saveDeviceDataSource(DeviceObjectDataSource deviceObjectDataSource) {
		// Check parameters
		checkNotNull(deviceObjectDataSource);
		checkNotNull(deviceObjectDataSource.getDeviceObject(), "DeviceObject is null");
		checkNotNull(deviceObjectDataSource.getSubscrDataSource(), "SubscrDataSource is null");
		checkArgument(!deviceObjectDataSource.getDeviceObject().isNew());

		// Additional init
		deviceObjectDataSource.setSubscrDataSourceId(deviceObjectDataSource.getSubscrDataSource().getId());

		// Check current Active data source
		List<DeviceObjectDataSource> currentActiveList = selectActiveDeviceObjectDataSource(
				deviceObjectDataSource.getDeviceObject().getId());

		Optional<DeviceObjectDataSource> checkActiveDataSource = currentActiveList.stream()
				.filter(i -> i.getSubscrDataSource().getId()
						.equals(deviceObjectDataSource.getSubscrDataSource().getId()))
				.sorted((a, b) -> b.getId().compareTo(a.getId())).findFirst();

		// Verify if current DataSource is active
		if (checkActiveDataSource.isPresent() && Boolean.TRUE.equals(deviceObjectDataSource.getIsActive())) {
			DeviceObjectDataSource activeDataSource = checkActiveDataSource.get();
			activeDataSource.setSubscrDataSourceAddr(deviceObjectDataSource.getSubscrDataSourceAddr());
			activeDataSource.setDataSourceTable(deviceObjectDataSource.getDataSourceTable());
			activeDataSource.setDataSourceTable1h(deviceObjectDataSource.getDataSourceTable1h());
			activeDataSource.setDataSourceTable24h(deviceObjectDataSource.getDataSourceTable24h());
			return deviceObjectDataSourceRepository.save(activeDataSource);
		}

		if (Boolean.FALSE.equals(deviceObjectDataSource.getIsActive())) {
			deviceObjectDataSource.setIsActive(null);
		}

		// Make other DataSources inactive
		if (Boolean.TRUE.equals(deviceObjectDataSource.getIsActive()) && !currentActiveList.isEmpty()) {
			currentActiveList.stream().filter(i -> !i.getId().equals(deviceObjectDataSource.getId()))
					.forEach(i -> i.setIsActive(null));
			deviceObjectDataSourceRepository.save(currentActiveList);
		}
		/////
		return deviceObjectDataSourceRepository.save(deviceObjectDataSource);
	}

	/**
	 * 
	 * @param deviceObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public void makeDeviceDataSourceInactive(Long deviceObjectId) {
		List<DeviceObjectDataSource> deviceObjectDataSources = selectActiveDeviceObjectDataSource(deviceObjectId);
		deviceObjectDataSources.forEach(i -> i.setIsActive(null));
		deviceObjectDataSourceRepository.save(deviceObjectDataSources);
	}
}
