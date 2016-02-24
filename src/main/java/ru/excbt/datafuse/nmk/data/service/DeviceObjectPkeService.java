package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectPkeWarn;
import ru.excbt.datafuse.nmk.data.model.keyname.DeviceObjectPkeType;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectPkeLimitRepository;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectPkeWarnRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.DeviceObjectPkeTypeRepository;

/**
 * Сервис для работы с ПКЭ
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.02.2016
 *
 */
@Service
public class DeviceObjectPkeService {

	private final static int DEFAULT_MAX_EVENTS = 1000;
	private final static PageRequest DEFAULT_MAX_EVENTS_PAGE_REQUEST = new PageRequest(0, DEFAULT_MAX_EVENTS);

	@Autowired
	private DeviceObjectPkeLimitRepository deviceObjectPkeLimitRepository;

	@Autowired
	private DeviceObjectPkeWarnRepository deviceObjectPkeWarnRepository;

	@Autowired
	private DeviceObjectPkeTypeRepository deviceObjectPkeTypeRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceObjectPkeType> selectDeviceObjectPkeType() {
		return Lists.newArrayList(deviceObjectPkeTypeRepository.findAll());
	}

	/**
	 * 
	 * @param deviceObjectId
	 * @param period
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceObjectPkeWarn> selectDeviceObjectPkeWarn(long deviceObjectId, LocalDatePeriod period) {
		checkNotNull(period);
		checkArgument(period.isValidEq());
		List<DeviceObjectPkeWarn> resultList = deviceObjectPkeWarnRepository.selectDeviceObjectWarn(deviceObjectId,
				period.getDateFrom(), period.getDateTo(), DEFAULT_MAX_EVENTS_PAGE_REQUEST);
		return resultList;
	}

}
