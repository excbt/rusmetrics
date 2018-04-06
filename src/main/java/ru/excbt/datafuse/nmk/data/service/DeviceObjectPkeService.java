package ru.excbt.datafuse.nmk.data.service;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectPkeWarn;
import ru.excbt.datafuse.nmk.data.model.QDeviceObjectPkeWarn;
import ru.excbt.datafuse.nmk.data.model.keyname.DeviceObjectPkeType;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectPkeLimitRepository;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectPkeWarnRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.DeviceObjectPkeTypeRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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

	private static final Logger logger = LoggerFactory.getLogger(DeviceObjectPkeService.class);

	private final static int DEFAULT_MAX_EVENTS = 1000;
	private final static PageRequest DEFAULT_MAX_EVENTS_PAGE_REQUEST = new PageRequest(0, DEFAULT_MAX_EVENTS);

	private final DeviceObjectPkeLimitRepository deviceObjectPkeLimitRepository;

	private final DeviceObjectPkeWarnRepository deviceObjectPkeWarnRepository;

	private final DeviceObjectPkeTypeRepository deviceObjectPkeTypeRepository;

    public DeviceObjectPkeService(DeviceObjectPkeLimitRepository deviceObjectPkeLimitRepository, DeviceObjectPkeWarnRepository deviceObjectPkeWarnRepository, DeviceObjectPkeTypeRepository deviceObjectPkeTypeRepository) {
        this.deviceObjectPkeLimitRepository = deviceObjectPkeLimitRepository;
        this.deviceObjectPkeWarnRepository = deviceObjectPkeWarnRepository;
        this.deviceObjectPkeTypeRepository = deviceObjectPkeTypeRepository;
    }

    public static class PkeWarnSearchConditions {
		private final long deviceObjectId;
		private final LocalDatePeriod period;
		private final List<String> pkeTypeKeynames = new ArrayList<>();

		public PkeWarnSearchConditions(long deviceObjectId, LocalDatePeriod localDatePeriod) {
			this.deviceObjectId = deviceObjectId;
			this.period = localDatePeriod;
		}

		public void initPkeTypeKeynames(List<String> pkeTypeKeynames) {
			this.pkeTypeKeynames.clear();
			if (pkeTypeKeynames != null) {
				this.pkeTypeKeynames.addAll(pkeTypeKeynames);
			}
		}

		public void initPkeTypeKeynames(String[] pkeTypeKeynames) {
			this.pkeTypeKeynames.clear();
			if (pkeTypeKeynames != null) {
				this.pkeTypeKeynames.addAll(Arrays.asList(pkeTypeKeynames));
			}
		}
	}

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

	/**
	 *
	 * @param searchConditions
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceObjectPkeWarn> selectDeviceObjectPkeWarn(PkeWarnSearchConditions searchConditions) {


        QDeviceObjectPkeWarn qDeviceObjectPkeWarn = QDeviceObjectPkeWarn.deviceObjectPkeWarn;

        BooleanBuilder where = new BooleanBuilder();
        where.and(qDeviceObjectPkeWarn.deviceObjectId.eq(searchConditions.deviceObjectId));

        if (searchConditions.period.isValidEq()) {
            where.and(
                qDeviceObjectPkeWarn.warnStartDate.between(searchConditions.period.getDateFrom(), searchConditions.period.getDateTo())
                .or(qDeviceObjectPkeWarn.warnEndDate.between(searchConditions.period.getDateFrom(), searchConditions.period.getDateTo()))
            );
        }

        if (!searchConditions.pkeTypeKeynames.isEmpty()) {
            where.and(qDeviceObjectPkeWarn.deviceObjectPkeTypeKeyname.in(searchConditions.pkeTypeKeynames));
        }

		Page<DeviceObjectPkeWarn> resultPage = deviceObjectPkeWarnRepository.findAll(where,
				DEFAULT_MAX_EVENTS_PAGE_REQUEST);

		return resultPage.getContent();
	}


}
