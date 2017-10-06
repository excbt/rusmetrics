package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableSet;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterAbs_Csv;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterTotals;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.TimeDetailLastDate;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataHWaterRepository;
import ru.excbt.datafuse.nmk.service.utils.ColumnHelper;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.utils.FileWriterUtils;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

/**
 * Сервис по работе с данными по горячей воде
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.03.2015
 *
 */
@Service
public class ContServiceDataHWaterService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(ContServiceDataHWaterService.class);

	private static final PageRequest LIMIT1_PAGE_REQUEST = new PageRequest(0, 1);

	private static final Set<String> HWATER_SERVICE_TYPE_SET = ImmutableSet.of(ContServiceTypeKey.CW.getKeyname(),
			ContServiceTypeKey.HW.getKeyname(), ContServiceTypeKey.HEAT.getKeyname());

	@Autowired
	private ContServiceDataHWaterRepository contServiceDataHWaterRepository;

	@Autowired
	private ContZPointService contZPointService;

	@Autowired
	private DeviceObjectService deviceObjectService;

	@PersistenceContext(unitName = "nmk-p")
	private EntityManager em;

	@Autowired
	private HWatersCsvService hWatersCsvService;

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataHWater> selectByContZPoint(long contZpointId, TimeDetailKey timeDetail,
			PageRequest pageRequest) {
		checkArgument(contZpointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(pageRequest);

		Page<ContServiceDataHWater> page = contServiceDataHWaterRepository.selectByZPoint(contZpointId,
				timeDetail.getKeyname(), pageRequest);
		return page.getContent();
	}

	/**
	 *
	 * @param contZpointId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	//@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	private List<ContServiceDataHWater> selectByContZPoint(long contZpointId, TimeDetailKey timeDetail,
			DateTime beginDate, DateTime endDate) {
		checkArgument(contZpointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(beginDate, "beginDate is null");
		checkNotNull(endDate, "endDate is null");
		checkArgument(beginDate.compareTo(endDate) <= 0);

		return contServiceDataHWaterRepository.selectByZPoint(contZpointId, timeDetail.getKeyname(), beginDate.toDate(),
				endDate.toDate());
	}

	/**
	 *
	 * @param contZpointId
	 * @param timeDetail
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataHWater> selectByContZPoint(long contZpointId, TimeDetailKey timeDetail, Date beginDate,
			Date endDate) {
		checkArgument(contZpointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(beginDate, "beginDate is null");
		checkNotNull(endDate, "endDate is null");
		checkArgument(beginDate.compareTo(endDate) <= 0);

		return contServiceDataHWaterRepository.selectByZPoint(contZpointId, timeDetail.getKeyname(), beginDate,
				endDate);
	}

	/**
	 *
	 * @param contZpointId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataHWater> selectByContZPoint(long contZpointId, TimeDetailKey timeDetail,
			LocalDateTime beginDate, LocalDateTime endDate) {
		checkArgument(contZpointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(beginDate, "beginDate is null");
		checkNotNull(endDate, "endDate is null");
		checkArgument(beginDate.compareTo(endDate) <= 0);

		return contServiceDataHWaterRepository.selectByZPoint(contZpointId, timeDetail.getKeyname(), beginDate.toDate(),
				endDate.toDate());
	}

	/**
	 *
	 * @param contZpointId
	 * @param timeDetail
	 * @param datePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataHWater> selectByContZPoint(long contZpointId, TimeDetailKey timeDetail,
			LocalDatePeriod datePeriod) {
		checkArgument(contZpointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(datePeriod, "beginDate is null");
		checkArgument(datePeriod.isValid());

		return contServiceDataHWaterRepository.selectByZPoint(contZpointId, timeDetail.getKeyname(),
				datePeriod.getDateFrom(), datePeriod.getDateTo());
	}

	/**
	 *
	 * @param contZpointId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Page<ContServiceDataHWater> selectByContZPoint(long contZpointId, TimeDetailKey timeDetail,
			DateTime beginDate, DateTime endDate, PageRequest pageRequest) {
		checkArgument(contZpointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(beginDate, "beginDate is null");
		checkNotNull(endDate, "endDate is null");
		checkArgument(beginDate.compareTo(endDate) <= 0);
		checkNotNull(pageRequest);

		return contServiceDataHWaterRepository.selectByZPoint(contZpointId, timeDetail.getKeyname(), beginDate.toDate(),
				endDate.toDate(), pageRequest);
	}

	/**
	 *
	 * @param contZpointId
	 * @param timeDetail
	 * @param datePeriod
	 * @param pageable
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Page<ContServiceDataHWater> selectByContZPoint(long contZpointId, TimeDetailKey timeDetail,
			LocalDatePeriod datePeriod, PageRequest pageRequest) {
		checkArgument(contZpointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(datePeriod, "beginDate is null");
		checkArgument(datePeriod.isValid());
		checkNotNull(pageRequest);

		return contServiceDataHWaterRepository.selectByZPoint(contZpointId, timeDetail.getKeyname(),
				datePeriod.getDateFrom(), datePeriod.getDateTo(), pageRequest);
	}

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataHWater selectLastData(long contZpointId) {
		checkArgument(contZpointId > 0);
		List<ContServiceDataHWater> resultList = contServiceDataHWaterRepository.selectLastDataByZPoint(contZpointId,
				LIMIT1_PAGE_REQUEST);
		return resultList.size() > 0 ? resultList.get(0) : null;
	}

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Date selectLastDataDate(long contZpointId, Date fromDateTime) {
		checkArgument(contZpointId > 0);

		Date actialFromDate = fromDateTime;
		if (actialFromDate == null) {
			actialFromDate = JodaTimeUtils.startOfDay(DateTime.now().minusDays(3)).toDate();
		} else {
			logger.debug("MinCheck: {}", actialFromDate);
		}

		List<ContServiceDataHWater> resultList = contServiceDataHWaterRepository.selectLastDataByZPoint(contZpointId,
				actialFromDate, LIMIT1_PAGE_REQUEST);

		if (resultList.size() == 0) {
			resultList = contServiceDataHWaterRepository.selectLastDataByZPoint(contZpointId, LIMIT1_PAGE_REQUEST);
		}

		checkNotNull(resultList);
		return resultList.size() > 0 ? resultList.get(0).getDataDate() : null;
	}

	/**
	 *
	 * @param contZpointId
	 * @param fromDateTime
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataHWater> selectLastDataFromDate(Long contZpointId, String timeDetailType,
			java.time.LocalDate fromDateTime) {

		checkNotNull(contZpointId);

		checkArgument(contZpointId > 0);

		List<ContServiceDataHWater> resultList = contServiceDataHWaterRepository.selectLastDataByZPoint(contZpointId,
				timeDetailType, LocalDateUtils.asDate(fromDateTime), LIMIT1_PAGE_REQUEST);
		return resultList;

	}

	/**
	 *
	 * @param contZpointId
	 * @param fromDateTime
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Date selectLastDataDate(long contZpointId) {
		return selectLastDataDate(contZpointId, null);
	}

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Date selectAnyDataDate(long contZpointId) {
		checkArgument(contZpointId > 0);
		Page<ContServiceDataHWater> resultPage = contServiceDataHWaterRepository.selectAnyDataByZPoint(contZpointId,
				LIMIT1_PAGE_REQUEST);
		List<ContServiceDataHWater> resultList = resultPage.getContent();
		return resultList.size() > 0 ? resultList.get(0).getDataDate() : null;
	}

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Boolean selectExistsAnyData(long contZpointId) {
		checkArgument(contZpointId > 0);
		List<Long> resultList = contServiceDataHWaterRepository.selectExistsAnyDataByZPoint(contZpointId,
				LIMIT1_PAGE_REQUEST);
		return resultList.size() > 0;
	}

	/**
	 *
	 * @param contZpointId
	 * @param timeDetail
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataHWaterTotals selectContZPoint_Totals(long contZpointId, TimeDetailKey timeDetail,
			LocalDateTime beginDate, LocalDateTime endDate) {

		checkNotNull(timeDetail);
		checkNotNull(beginDate);
		checkNotNull(endDate);
		checkArgument(beginDate.compareTo(endDate) <= 0);

		logger.debug("selectContZPointTotals: contZPoint:{}; timeDetailType:{}; beginDate:{}; endDate:{}", contZpointId,
				timeDetail.getKeyname(), beginDate.toDate(), endDate.toDate());

		Query q1 = em.createQuery("SELECT sum(m_in) as m_in, sum(m_out) as m_out, sum(m_delta) as m_delta, "
				+ " sum(h_in) as h_in, sum(h_out) as h_out, sum(h_delta) as h_delta, "
				+ " sum(v_in) as v_in, sum(v_out) as v_out, sum(v_delta) as v_delta "
				+ " FROM ContServiceDataHWater hw " + " WHERE hw.timeDetailType = :timeDetailType "
				+ " AND hw.contZPoint.id = :contZpointId " + " AND hw.dataDate >= :beginDate "
				+ " AND hw.dataDate <= :endDate AND hw.deleted = 0");

		q1.setParameter("timeDetailType", timeDetail.getKeyname());
		q1.setParameter("contZpointId", contZpointId);
		q1.setParameter("beginDate", beginDate.toDate());
		q1.setParameter("endDate", endDate.toDate());

		Object[] results = (Object[]) q1.getSingleResult();
		checkNotNull(results);

		ContServiceDataHWaterTotals result = new ContServiceDataHWaterTotals();

		// TODO
		result.setM_in((Double) results[0]);
		result.setM_out((Double) results[1]);
		result.setM_delta((Double) results[2]);

		result.setH_in((Double) results[3]);
		result.setH_out((Double) results[4]);
		result.setH_delta((Double) results[5]);

		result.setV_in((Double) results[6]);
		result.setV_out((Double) results[7]);
		result.setV_delta((Double) results[8]);

		result.setContZPointId(contZpointId);
		result.setBeginDate(beginDate.toDate());
		result.setEndDate(beginDate.toDate());

		result.setTimeDetailType(timeDetail.getKeyname());

		return result;
	}

	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataHWater selectContZPoint_Avgs(long contZpointId, TimeDetailKey timeDetail,
			LocalDatePeriod period) {

		checkNotNull(timeDetail);
		checkNotNull(period);
		checkArgument(period.isValidEq());

		String[] columns = new String[] { "t_in", "t_out", "t_cold", "t_outdoor", "m_in", "m_out", "m_delta", "v_in",
				"v_out", "v_delta", "h_in", "h_out", "h_delta", "p_in", "p_out", "p_delta", "workTime", "failTime" };

		ColumnHelper columnHelper = new ColumnHelper(columns, "avg(%s)");
		logger.debug("Colums: {}", columnHelper.build());

		StringBuilder sqlString = new StringBuilder();
		sqlString.append(" SELECT ");
		sqlString.append(columnHelper.build());
		sqlString.append(" FROM ");
		sqlString.append(" ContServiceDataHWater hw ");
		sqlString.append(" WHERE hw.timeDetailType = :timeDetailType ");
		sqlString.append(" AND hw.contZPoint.id = :contZpointId ");
		sqlString.append(" AND hw.dataDate >= :beginDate ");
		sqlString.append(" AND hw.dataDate <= :endDate ");
		sqlString.append(" AND hw.deleted = 0 ");
		logger.debug("Sql: {}", sqlString.toString());

		Query q1 = em.createQuery(sqlString.toString());

		q1.setParameter("timeDetailType", timeDetail.getKeyname());
		q1.setParameter("contZpointId", contZpointId);
		q1.setParameter("beginDate", period.getDateFrom());
		q1.setParameter("endDate", period.getDateTo());

		Object[] results = (Object[]) q1.getSingleResult();
		checkNotNull(results);

		ContServiceDataHWater result = new ContServiceDataHWater();
		result.setT_in(columnHelper.getResultDouble(results, "t_in"));
		result.setT_out(columnHelper.getResultDouble(results, "t_out"));
		result.setT_cold(columnHelper.getResultDouble(results, "t_cold"));
		result.setT_outdoor(columnHelper.getResultDouble(results, "t_outdoor"));
		result.setM_in(columnHelper.getResultDouble(results, "m_in"));
		result.setM_out(columnHelper.getResultDouble(results, "m_out"));
		result.setM_delta(columnHelper.getResultDouble(results, "m_delta"));
		result.setV_in(columnHelper.getResultDouble(results, "v_in"));
		result.setV_out(columnHelper.getResultDouble(results, "v_out"));
		result.setV_delta(columnHelper.getResultDouble(results, "v_delta"));
		result.setH_in(columnHelper.getResultDouble(results, "h_in"));
		result.setH_out(columnHelper.getResultDouble(results, "h_out"));
		result.setH_delta(columnHelper.getResultDouble(results, "h_delta"));
		result.setP_in(columnHelper.getResultDouble(results, "p_in"));
		result.setP_out(columnHelper.getResultDouble(results, "p_out"));
		result.setP_delta(columnHelper.getResultDouble(results, "p_delta"));
		result.setWorkTime(columnHelper.getResultDouble(results, "workTime"));
		result.setFailTime(columnHelper.getResultDouble(results, "failTime"));

		return result;

	}

	/**
	 *
	 * @param contZpointId
	 * @param localDateTime
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	private ContServiceDataHWater selectLastAbsData(long contZpointId, LocalDateTime localDateTime) {

		checkNotNull(localDateTime);

		String[] timeDetails = { // timeDetail.getAbsPair()
				TimeDetailKey.TYPE_1H.getAbsPair(), TimeDetailKey.TYPE_24H.getAbsPair() };

		List<ContServiceDataHWater> dataList = contServiceDataHWaterRepository
				.selectLastDetailDataByZPoint(contZpointId, timeDetails, localDateTime.toDate(), LIMIT1_PAGE_REQUEST);

		return dataList.size() > 0 ? dataList.get(0) : null;
	}

	/**
	 *
	 * @param contZpointId
	 * @param localDateTime
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataHWater selectLastAbsData(long contZpointId, TimeDetailKey timeDetail,
			LocalDateTime localDateTime, boolean isEndDate) {

		checkNotNull(localDateTime);
		checkNotNull(timeDetail);

		String[] dataTimeDetails = { timeDetail.getKeyname() };

		Date dataDateLimit;

		if (isEndDate) {
			List<ContServiceDataHWater> dataList = contServiceDataHWaterRepository.selectLastDetailDataByZPoint(
					contZpointId, dataTimeDetails, localDateTime.toDate(), LIMIT1_PAGE_REQUEST);

			if (dataList.isEmpty()) {
				return null;
			}

			dataDateLimit = dataList.get(0).getDataDate();
			// Truncate dataDateLimit
			LocalDateTime ldt = new LocalDateTime(dataDateLimit);
			dataDateLimit = JodaTimeUtils.startOfDay(ldt.plusDays(1)).toDate();
		} else {
			dataDateLimit = localDateTime.toDate();
		}

		String[] integratorTimeDetails = { TimeDetailKey.TYPE_1H.getAbsPair(), TimeDetailKey.TYPE_24H.getAbsPair() };
		List<ContServiceDataHWater> integratorList = null;
		if (isEndDate) {
			integratorList = contServiceDataHWaterRepository.selectLastDetailDataByZPoint(contZpointId,
					integratorTimeDetails, dataDateLimit, LIMIT1_PAGE_REQUEST);
		} else {
			integratorList = contServiceDataHWaterRepository.selectFirstDetailDataByZPoint(contZpointId,
					integratorTimeDetails, dataDateLimit, LIMIT1_PAGE_REQUEST);
		}
		;

		return integratorList.size() > 0 ? integratorList.get(0) : null;
	}

	/**
	 *
	 * @param contZpointId
	 * @param timeDetail
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataHWaterAbs_Csv> selectDataAbs_Csv(long contZpointId, TimeDetailKey timeDetail,
			DateTime beginDate, DateTime endDate) {

		List<ContServiceDataHWater> srcDataList = selectByContZPoint(contZpointId, timeDetail, beginDate, endDate);

		List<ContServiceDataHWaterAbs_Csv> cvsDataList = new ArrayList<>();
		try {

			for (ContServiceDataHWater data : srcDataList) {
				ContServiceDataHWaterAbs_Csv cvsData;
				cvsData = ContServiceDataHWaterAbs_Csv.newInstance(data);
				ContServiceDataHWater abs = selectLastAbsData(data.getContZPointId(), timeDetail,
						new LocalDateTime(data.getDataDate()), false);
				cvsData.copyAbsData(abs);
				cvsDataList.add(cvsData);
			}

		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("Can't create intance of {}: {}", ContServiceDataHWaterAbs_Csv.class, e);
			cvsDataList.clear();
		}

		return cvsDataList;
	}

	/**
	 *
	 * @param contZpointId
	 * @param inData
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	public void insertManualLoadDataHWater(Long contZpointId, List<ContServiceDataHWater> inData, File outFile) {

		checkNotNull(contZpointId);
		checkNotNull(inData);
		checkArgument(inData.size() > 0);

		ContZPoint zpoint = contZPointService.findOne(contZpointId);

		checkNotNull(zpoint, String.format("ContZPoint with id:%d is not found", contZpointId));

		checkState(BooleanUtils.isTrue(zpoint.getIsManualLoading()),
				String.format("Manual Loading for ContZPoint with id:%d is not allowed", contZpointId));

		// Device Object Check And Save
		DeviceObject deviceObject = null;

		if (zpoint.getDeviceObjects().isEmpty()) {

			logger.debug("Device Object is not found. Create new");

			deviceObject = deviceObjectService.createManualDeviceObject();
			logger.debug("Cont Object is saved. Id:{}", deviceObject.getId());

			zpoint.getDeviceObjects().add(deviceObject);
			contZPointService.updateContZPoint(zpoint);

			logger.debug("ContZPoint is saved. Id:{}", zpoint.getId());
		} else {
			deviceObject = zpoint.getDeviceObjects().get(0);
		}

		Optional<ContServiceDataHWater> checkIsNewElements = inData.stream().filter((i) -> !i.isNew()).findAny();

		checkState(!checkIsNewElements.isPresent(), "Elements in data list is not new");

		final DeviceObject dObject = deviceObject;
		inData.forEach((d) -> {
			d.setContZPointId(contZpointId);
			//d.setTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname());
			d.setDeviceObject(dObject);
		});

		contServiceDataHWaterRepository.save(inData);

	}

	/**
	 *
	 * @param contZpointId
	 * @param localDatePeriod
	 * @param outFile
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	public List<ContServiceDataHWater> deleteManualDataHWater(Long contZpointId, LocalDatePeriod localDatePeriod,
			TimeDetailKey timeDetailKey, File outFile) {

		checkNotNull(contZpointId);
		checkNotNull(localDatePeriod);
		checkNotNull(timeDetailKey);

		ContZPoint zpoint = contZPointService.findOne(contZpointId);

		checkNotNull(zpoint, String.format("ContZPoint with id:%d is not found", contZpointId));

		checkState(BooleanUtils.isTrue(zpoint.getIsManualLoading()),
				String.format("Manual Loading and Deleting for ContZPoint with id:%d is not allowed", contZpointId));

		List<ContServiceDataHWater> deleteCandidate = selectByContZPoint(contZpointId, timeDetailKey, localDatePeriod);

		try {
			ByteArrayInputStream is = new ByteArrayInputStream(hWatersCsvService.writeDataHWaterToCsv(deleteCandidate));

			@SuppressWarnings("unused")
			String digestMD5 = FileWriterUtils.writeFile(is, outFile);

		} catch (IOException e) {
			throw new PersistenceException(
					String.format("Can't save into file (%s) cadidate for delete rows for contZpointId=%d",
							outFile.getAbsolutePath(), contZpointId));
		}

		contServiceDataHWaterRepository.delete(deleteCandidate);

		return deleteCandidate;
	}

    /**
     *
     * @param contZpointId
     * @return
     */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<TimeDetailLastDate> selectTimeDetailLastDate(long contZpointId) {
		checkArgument(contZpointId > 0);

		List<TimeDetailLastDate> resultList = new ArrayList<>();

		List<Object[]> qryResultList = contServiceDataHWaterRepository.selectTimeDetailLastDataByZPoint(contZpointId);

		for (Object[] row : qryResultList) {

			logger.info("Data Type: {}", row[1].getClass());

			String timeDetail = (String) row[0];
			Timestamp lastDate = (Timestamp) row[1];

			TimeDetailLastDate item = new TimeDetailLastDate(timeDetail, lastDate);
			resultList.add(item);
		}

		return resultList;

	}

	/**
	 *
	 * @param contZpointIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public HashMap<Long, List<TimeDetailLastDate>> selectTimeDetailLastDateMap(List<Long> contZpointIds) {
		checkArgument(contZpointIds != null);

		HashMap<Long, List<TimeDetailLastDate>> resultMap = !contZpointIds.isEmpty()
				? ContServiceDataUtil.collectContZPointTimeDetailTypes(
						contServiceDataHWaterRepository.selectTimeDetailLastDataByZPoint(contZpointIds))
				: new HashMap<>();

		return resultMap;

	}

	/**
	 *
	 * @param idServiceTypePairs
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public HashMap<Long, List<TimeDetailLastDate>> selectTimeDetailLastDateMapByPair(
			List<Pair<String, Long>> idServiceTypePairs) {
		checkArgument(idServiceTypePairs != null);

		List<Long> contZpointIds = idServiceTypePairs.stream()
				.filter(i -> HWATER_SERVICE_TYPE_SET.contains(i.getLeft())).map(i -> i.getRight())
				.collect(Collectors.toList());

		return selectTimeDetailLastDateMap(contZpointIds);

	}

}
