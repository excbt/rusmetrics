package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataJson;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectDataJsonRepository;

@Service
@Transactional
@Deprecated
public class DeviceObjectDataJsonService {

	@Autowired
	private DeviceObjectDataJsonRepository deviceObjectDataJsonRepository;

	/**
	 * 
	 * @param deviceObjectId
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<DeviceObjectDataJson> selectDeviceObjectDataJson(
			long deviceObjectId, TimeDetailKey timeDetail, Pageable pageable) {

		checkNotNull(timeDetail);
		checkNotNull(pageable);

		return deviceObjectDataJsonRepository.selectByDeviceObject(
				deviceObjectId, timeDetail.getKeyname(), pageable);
	}

	/**
	 * 
	 * @param deviceObjectId
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<DeviceObjectDataJson> selectDeviceObjectDataJson(
			long deviceObjectId, TimeDetailKey timeDetail,
			LocalDateTime fromDate, Pageable pageable) {

		checkNotNull(timeDetail);
		checkNotNull(pageable);
		checkNotNull(fromDate);

		return deviceObjectDataJsonRepository.selectByDeviceObject(
				deviceObjectId, timeDetail.getKeyname(),
				Date.from(fromDate.atZone(ZoneId.systemDefault()).toInstant()),
				pageable);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public DeviceObjectDataJson findOne(long id) {
		return deviceObjectDataJsonRepository.findOne(id);
	}

}
