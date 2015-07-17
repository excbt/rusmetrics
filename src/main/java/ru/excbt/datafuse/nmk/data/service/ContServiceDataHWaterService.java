package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterAbs_Csv;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterTotals;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataHWaterRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;

@Service
@Transactional
public class ContServiceDataHWaterService implements SecuredRoles {

	private static final Logger logger = LoggerFactory
			.getLogger(ContServiceDataHWaterService.class);

	private static final PageRequest LIMIT1_PAGE_REQUEST = new PageRequest(0, 1);

	@Autowired
	private ContServiceDataHWaterRepository contServiceDataHWaterRepository;

	@Autowired
	private ContZPointService contZPointService;

	@Autowired
	private DeviceObjectService deviceObjectService;

	@PersistenceContext
	private EntityManager em;

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContServiceDataHWater> selectByContZPoint(long contZPointId,
			TimeDetailKey timeDetail) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		return contServiceDataHWaterRepository.selectByZPoint(contZPointId,
				timeDetail.getKeyname());
	}

	/**
	 * 
	 * @param contZPointId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(readOnly = true)
	private List<ContServiceDataHWater> selectByContZPoint(long contZPointId,
			TimeDetailKey timeDetail, DateTime beginDate, DateTime endDate) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(beginDate, "beginDate is null");
		checkNotNull(endDate, "endDate is null");
		checkArgument(beginDate.compareTo(endDate) <= 0);

		return contServiceDataHWaterRepository.selectByZPoint(contZPointId,
				timeDetail.getKeyname(), beginDate.toDate(), endDate.toDate());
	}

	/**
	 * 
	 * @param contZPointId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContServiceDataHWater> selectByContZPoint(long contZPointId,
			TimeDetailKey timeDetail, LocalDateTime beginDate,
			LocalDateTime endDate) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(beginDate, "beginDate is null");
		checkNotNull(endDate, "endDate is null");
		checkArgument(beginDate.compareTo(endDate) <= 0);

		return contServiceDataHWaterRepository.selectByZPoint(contZPointId,
				timeDetail.getKeyname(), beginDate.toDate(), endDate.toDate());
	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param datePeriod
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContServiceDataHWater> selectByContZPoint(long contZPointId,
			TimeDetailKey timeDetail, LocalDatePeriod datePeriod) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(datePeriod, "beginDate is null");
		checkArgument(datePeriod.isValid());

		return contServiceDataHWaterRepository.selectByZPoint(contZPointId,
				timeDetail.getKeyname(), datePeriod.getDateFrom(),
				datePeriod.getDateTo());
	}

	/**
	 * 
	 * @param contZPointId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<ContServiceDataHWater> selectByContZPoint(long contZPointId,
			TimeDetailKey timeDetail, DateTime beginDate, DateTime endDate,
			Pageable pageable) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(beginDate, "beginDate is null");
		checkNotNull(endDate, "endDate is null");
		checkArgument(beginDate.compareTo(endDate) <= 0);
		checkNotNull(pageable);

		return contServiceDataHWaterRepository.selectByZPoint(contZPointId,
				timeDetail.getKeyname(), beginDate.toDate(), endDate.toDate(),
				pageable);
	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param datePeriod
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<ContServiceDataHWater> selectByContZPoint(long contZPointId,
			TimeDetailKey timeDetail, LocalDatePeriod datePeriod,
			Pageable pageable) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(datePeriod, "beginDate is null");
		checkArgument(datePeriod.isValid());
		checkNotNull(pageable);

		return contServiceDataHWaterRepository.selectByZPoint(contZPointId,
				timeDetail.getKeyname(), datePeriod.getDateFrom(),
				datePeriod.getDateTo(), pageable);
	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(readOnly = true)
	public ContServiceDataHWater selectLastData(long contZPointId) {
		checkArgument(contZPointId > 0);
		List<ContServiceDataHWater> resultList = contServiceDataHWaterRepository
				.selectLastDataByZPoint(contZPointId, LIMIT1_PAGE_REQUEST);
		return resultList.size() > 0 ? resultList.get(0) : null;
	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Date selectLastDataDate(long contZPointId, Date fromDateTime) {
		checkArgument(contZPointId > 0);

		Date actialFromDate = fromDateTime;
		if (actialFromDate == null) {
			actialFromDate = JodaTimeUtils.startOfDay(
					DateTime.now().minusDays(3)).toDate();
		} else {
			logger.debug("MinCheck: {}", actialFromDate);
		}

		List<ContServiceDataHWater> resultList = contServiceDataHWaterRepository
				.selectLastDataByZPoint(contZPointId, actialFromDate,
						LIMIT1_PAGE_REQUEST);

		if (resultList.size() == 0) {
			resultList = contServiceDataHWaterRepository
					.selectLastDataByZPoint(contZPointId, LIMIT1_PAGE_REQUEST);
		}

		checkNotNull(resultList);
		return resultList.size() > 0 ? resultList.get(0).getDataDate() : null;
	}

	/**
	 * 
	 * @param contZPointId
	 * @param fromDateTime
	 * @return
	 */
	@Transactional(readOnly = true)
	public Date selectLastDataDate(long contZPointId) {
		return selectLastDataDate(contZPointId, null);
	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Date selectAnyDataDate(long contZPointId) {
		checkArgument(contZPointId > 0);
		List<ContServiceDataHWater> resultList = contServiceDataHWaterRepository
				.selectAnyDataByZPoint(contZPointId, LIMIT1_PAGE_REQUEST);
		return resultList.size() > 0 ? resultList.get(0).getDataDate() : null;
	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Boolean selectExistsAnyData(long contZPointId) {
		checkArgument(contZPointId > 0);
		List<Long> resultList = contServiceDataHWaterRepository
				.selectExistsAnyDataByZPoint(contZPointId, LIMIT1_PAGE_REQUEST);
		return resultList.size() > 0;
	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(readOnly = true)
	public ContServiceDataHWaterTotals selectContZPointTotals(
			long contZPointId, TimeDetailKey timeDetail,
			LocalDateTime beginDate, LocalDateTime endDate) {

		checkNotNull(timeDetail);
		checkNotNull(beginDate);
		checkNotNull(endDate);
		checkArgument(beginDate.compareTo(endDate) <= 0);

		logger.debug(
				"selectContZPointTotals: contZPoint:{}; timeDetailType:{}; beginDate:{}; endDate:{}",
				contZPointId, timeDetail.getKeyname(), beginDate.toDate(),
				endDate.toDate());

		Query q1 = em
				.createQuery("SELECT sum(m_in) as m_in, sum(m_out) as m_out, sum(m_delta) as m_delta, "
						+ " sum(h_in) as h_in, sum(h_out) as h_out, sum(h_delta) as h_delta, "
						+ " sum(v_in) as v_in, sum(v_out) as v_out, sum(v_delta) as v_delta "
						+ " FROM ContServiceDataHWater hw "
						+ " WHERE hw.timeDetailType = :timeDetailType "
						+ " AND hw.contZPoint.id = :contZPointId "
						+ " AND hw.dataDate >= :beginDate "
						+ " AND hw.dataDate <= :endDate ");

		q1.setParameter("timeDetailType", timeDetail.getKeyname());
		q1.setParameter("contZPointId", contZPointId);
		q1.setParameter("beginDate", beginDate.toDate());
		q1.setParameter("endDate", endDate.toDate());

		Object[] results = (Object[]) q1.getSingleResult();
		checkNotNull(results);

		ContServiceDataHWaterTotals result = new ContServiceDataHWaterTotals();

		result.setM_in((BigDecimal) results[0]);
		result.setM_out((BigDecimal) results[1]);
		result.setM_delta((BigDecimal) results[2]);

		result.setH_in((BigDecimal) results[3]);
		result.setH_out((BigDecimal) results[4]);
		result.setH_delta((BigDecimal) results[5]);

		result.setV_in((BigDecimal) results[6]);
		result.setV_out((BigDecimal) results[7]);
		result.setV_delta((BigDecimal) results[8]);

		result.setContZPointId(contZPointId);
		result.setBeginDate(beginDate.toDate());
		result.setEndDate(beginDate.toDate());

		result.setTimeDetailType(timeDetail.getKeyname());

		return result;
	}

	/**
	 * 
	 * @param contZPointId
	 * @param localDateTime
	 * @return
	 */
	@Transactional(readOnly = true)
	public ContServiceDataHWater selectLastAbsData(long contZPointId,
			LocalDateTime localDateTime) {

		checkNotNull(localDateTime);

		String[] timeDetails = {// timeDetail.getAbsPair()
		TimeDetailKey.TYPE_1H.getAbsPair(), TimeDetailKey.TYPE_24H.getAbsPair() };

		List<ContServiceDataHWater> dataList = contServiceDataHWaterRepository
				.selectLastDetailDataByZPoint(contZPointId, timeDetails,
						localDateTime.toDate(), LIMIT1_PAGE_REQUEST);

		return dataList.size() > 0 ? dataList.get(0) : null;
	}

	/**
	 * 
	 * @param contZPointId
	 * @param localDateTime
	 * @return
	 */
	@Transactional(readOnly = true)
	private ContServiceDataHWater selectLastAbsData(long contZPointId,
			TimeDetailKey timeDetail, LocalDateTime localDateTime) {

		checkNotNull(localDateTime);
		checkNotNull(timeDetail);

		String[] timeDetails = { // TimeDetailKey.TYPE_ABS.getKeyname(),
				// timeDetail.getAbsPair()
				TimeDetailKey.TYPE_1H.getAbsPair(),
				TimeDetailKey.TYPE_24H.getAbsPair() };

		List<ContServiceDataHWater> dataList = contServiceDataHWaterRepository
				.selectLastDetailDataByZPoint(contZPointId, timeDetails,
						localDateTime.toDate(), LIMIT1_PAGE_REQUEST);

		return dataList.size() > 0 ? dataList.get(0) : null;
	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContServiceDataHWaterAbs_Csv> selectDataAbs_Csv(
			long contZPointId, TimeDetailKey timeDetail, DateTime beginDate,
			DateTime endDate) {

		List<ContServiceDataHWater> srcDataList = selectByContZPoint(
				contZPointId, timeDetail, beginDate, endDate);

		List<ContServiceDataHWaterAbs_Csv> cvsDataList = new ArrayList<>();
		try {

			for (ContServiceDataHWater data : srcDataList) {
				ContServiceDataHWaterAbs_Csv cvsData;
				cvsData = ContServiceDataHWaterAbs_Csv.newInstance(data);
				ContServiceDataHWater abs = selectLastAbsData(
						data.getContZPointId(), timeDetail, new LocalDateTime(
								data.getDataDate()));
				cvsData.copyAbsData(abs);
				cvsDataList.add(cvsData);
			}

		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("Can't create intance of {}: {}",
					ContServiceDataHWaterAbs_Csv.class, e);
			cvsDataList.clear();
		}

		return cvsDataList;
	}

	/**
	 * 
	 * @param contZPointId
	 * @param inData
	 */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	public void manualLoadDataHWater(Long contZPointId,
			List<ContServiceDataHWater> inData) {

		checkNotNull(contZPointId);
		checkNotNull(inData);
		checkArgument(inData.size() > 0);

		ContZPoint zpoint = contZPointService.findContZPoint(contZPointId);

		checkNotNull(zpoint, String.format(
				"ContZPoint with id:%d is not found", contZPointId));

		checkState(
				BooleanUtils.isTrue(zpoint.getIsManualLoading()),
				String.format(
						"Manual Loading for ContZPoint with id:%d is not allowed",
						contZPointId));

		// Device Object Check And Save
		DeviceObject deviceObject = null;

		if (zpoint.getDeviceObjects().isEmpty()) {

			logger.debug("Device Object is not found. Create new");

			deviceObject = deviceObjectService.createPortalDeviceObject();
			logger.debug("Cont Object is saved. Id:{}", deviceObject.getId());

			zpoint.getDeviceObjects().add(deviceObject);
			contZPointService.saveContZPoint(zpoint);

			logger.debug("ContZPoint is saved. Id:{}", zpoint.getId());
		} else {
			deviceObject = zpoint.getDeviceObjects().get(0);
		}

		Optional<ContServiceDataHWater> checkIsNewElements = inData.stream()
				.filter((i) -> !i.isNew()).findAny();

		checkState(!checkIsNewElements.isPresent(),
				"Elements in data list is not new");

		final DeviceObject dObject = deviceObject;
		inData.forEach((d) -> {
			d.setContZPointId(contZPointId);
			d.setTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname());
			d.setDeviceObject(dObject);
		});

		contServiceDataHWaterRepository.save(inData);

	}

}
