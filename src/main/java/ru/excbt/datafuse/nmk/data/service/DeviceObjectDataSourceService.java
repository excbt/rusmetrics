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
	public List<DeviceObjectDataSource> selectActiveDeviceDataSource(Long deviceObjectId) {
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
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN })
	public DeviceObjectDataSource saveDeviceDataSource(DeviceObjectDataSource deviceObjectDataSource) {
		checkNotNull(deviceObjectDataSource);
		checkArgument(deviceObjectDataSource.getDeviceObjectId() != null);
		checkArgument(deviceObjectDataSource.getSubscrDataSourceId() != null);

		List<DeviceObjectDataSource> currentActiveList = selectActiveDeviceDataSource(
				deviceObjectDataSource.getDeviceObjectId());

		Optional<DeviceObjectDataSource> activeDataSources = currentActiveList.stream()
				.filter(i -> i.getSubscrDataSourceId().equals(deviceObjectDataSource.getSubscrDataSourceId()))
				.sorted((a, b) -> b.getId().compareTo(a.getId())).findFirst();

		if (activeDataSources.isPresent() && Boolean.TRUE.equals(deviceObjectDataSource.getIsActive())) {
			DeviceObjectDataSource currentRec = activeDataSources.get();
			currentRec.setSubscrDataSourceAddr(deviceObjectDataSource.getSubscrDataSourceAddr());
			return deviceObjectDataSourceRepository.save(currentRec);
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

		return deviceObjectDataSourceRepository.save(deviceObjectDataSource);
	}

	/**
	 * 
	 * @param deviceObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN })
	public void makeDeviceDataSourceInactive(Long deviceObjectId) {
		List<DeviceObjectDataSource> deviceObjectDataSources = selectActiveDeviceDataSource(deviceObjectId);
		deviceObjectDataSources.forEach(i -> i.setIsActive(null));
		deviceObjectDataSourceRepository.save(deviceObjectDataSources);
	}
}
