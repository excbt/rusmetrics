package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectPkeWarn;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectPkeWarn_;
import ru.excbt.datafuse.nmk.data.model.keyname.DeviceObjectPkeType;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectPkeLimitRepository;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectPkeWarnRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.DeviceObjectPkeTypeRepository;
import ru.excbt.datafuse.nmk.service.utils.DBSpecUtil;

/**
 * Сервис для работы с ПКЭ
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.02.2016
 *
 */
@Service
public class DeviceObjectPkeService extends AbstractService {

	private static final Logger logger = LoggerFactory.getLogger(DeviceObjectPkeService.class);

	private final static int DEFAULT_MAX_EVENTS = 1000;
	private final static PageRequest DEFAULT_MAX_EVENTS_PAGE_REQUEST = new PageRequest(0, DEFAULT_MAX_EVENTS);

	@Autowired
	private DeviceObjectPkeLimitRepository deviceObjectPkeLimitRepository;

	@Autowired
	private DeviceObjectPkeWarnRepository deviceObjectPkeWarnRepository;

	@Autowired
	private DeviceObjectPkeTypeRepository deviceObjectPkeTypeRepository;

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

		List<Specification<DeviceObjectPkeWarn>> specList = Arrays.asList( //
				specDeviceObjectId(searchConditions.deviceObjectId), //
				specDatePeriod(searchConditions.period), //
				specPkeTypes(searchConditions.pkeTypeKeynames));

		Specifications<DeviceObjectPkeWarn> specs = DBSpecUtil.specsAndFilterBuild(specList);

		Page<DeviceObjectPkeWarn> resultPage = deviceObjectPkeWarnRepository.findAll(specs,
				DEFAULT_MAX_EVENTS_PAGE_REQUEST);

		return resultPage.getContent();
	}

	/**
	 *
	 * @param deviceObjectId
	 * @return
	 */
	private static Specification<DeviceObjectPkeWarn> specDeviceObjectId(final long deviceObjectId) {
		return (root, query, cb) -> {
			return cb.equal(root.get(DeviceObjectPkeWarn_.deviceObjectId), deviceObjectId);
		};
	}

	/**
	 *
	 * @param period
	 * @return
	 */
	private static Specification<DeviceObjectPkeWarn> specDatePeriod(final LocalDatePeriod period) {
		return (root, query, cb) -> {

			if (period == null || period.isInvalidEq()) {
				return null;
			}

			return cb.or(
					cb.between(root.get(DeviceObjectPkeWarn_.warnStartDate), period.getDateFrom(), period.getDateTo()),
					cb.between(root.get(DeviceObjectPkeWarn_.warnEndDate), period.getDateFrom(), period.getDateTo()));
		};
	}

	/**
	 *
	 * @param pkeTypeKeynames
	 * @return
	 */
	private static Specification<DeviceObjectPkeWarn> specPkeTypes(final List<String> pkeTypeKeynames) {
		return (root, query, cb) -> {

			if (pkeTypeKeynames == null || pkeTypeKeynames.isEmpty()) {
				return null;
			}

			return root.get(DeviceObjectPkeWarn_.deviceObjectPkeTypeKeyname).in(pkeTypeKeynames);
		};
	}

}
