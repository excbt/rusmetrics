package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterAbs_Csv;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterTotals;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataHWaterRepository;
import ru.excbt.datafuse.nmk.data.service.support.DBRowUtils;
import ru.excbt.datafuse.nmk.data.service.support.HWatersCsvService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.utils.FileWriterUtils;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;

@Service
public class ContServiceDataHWaterService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(ContServiceDataHWaterService.class);

	private static final PageRequest LIMIT1_PAGE_REQUEST = new PageRequest(0, 1);

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

	private class ColumnHelper {
		private final String[] columns;
		private final String operator;
		private final List<String> columnList;

		ColumnHelper(String[] columns, String operator) {
			checkNotNull(columns);
			this.columns = columns;
			this.operator = operator;
			this.columnList = Collections.unmodifiableList(Arrays.asList(columns));
		}

		String build() {
			StringBuilder sb = new StringBuilder();
			for (String col : columns) {
				sb.append(String.format(operator, col));
				sb.append(" as ");
				sb.append(col);
				sb.append(',');
			}
			sb.delete(sb.length() - 1, sb.length());
			return sb.toString();
		}

		int indexOf(String column) {
			return columnList.indexOf(column);
		}

		BigDecimal getResult(Object[] results, String column) {
			int idx = indexOf(column);
			checkState(idx >= 0, "Invalid column index");
			Object value = results[idx];
			return DBRowUtils.asBigDecimal(value);

		}

	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataHWater> selectByContZPoint(long contZPointId, TimeDetailKey timeDetail) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		return contServiceDataHWaterRepository.selectByZPoint(contZPointId, timeDetail.getKeyname());
	}

	/**
	 * 
	 * @param contZPointId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	private List<ContServiceDataHWater> selectByContZPoint(long contZPointId, TimeDetailKey timeDetail,
			DateTime beginDate, DateTime endDate) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(beginDate, "beginDate is null");
		checkNotNull(endDate, "endDate is null");
		checkArgument(beginDate.compareTo(endDate) <= 0);

		return contServiceDataHWaterRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(), beginDate.toDate(),
				endDate.toDate());
	}

	/**
	 * 
	 * @param contZPointId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataHWater> selectByContZPoint(long contZPointId, TimeDetailKey timeDetail,
			LocalDateTime beginDate, LocalDateTime endDate) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(beginDate, "beginDate is null");
		checkNotNull(endDate, "endDate is null");
		checkArgument(beginDate.compareTo(endDate) <= 0);

		return contServiceDataHWaterRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(), beginDate.toDate(),
				endDate.toDate());
	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param datePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataHWater> selectByContZPoint(long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod datePeriod) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(datePeriod, "beginDate is null");
		checkArgument(datePeriod.isValid());

		return contServiceDataHWaterRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				datePeriod.getDateFrom(), datePeriod.getDateTo());
	}

	/**
	 * 
	 * @param contZPointId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Page<ContServiceDataHWater> selectByContZPoint(long contZPointId, TimeDetailKey timeDetail,
			DateTime beginDate, DateTime endDate, Pageable pageable) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(beginDate, "beginDate is null");
		checkNotNull(endDate, "endDate is null");
		checkArgument(beginDate.compareTo(endDate) <= 0);
		checkNotNull(pageable);

		return contServiceDataHWaterRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(), beginDate.toDate(),
				endDate.toDate(), pageable);
	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param datePeriod
	 * @param pageable
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Page<ContServiceDataHWater> selectByContZPoint(long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod datePeriod, Pageable pageable) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(datePeriod, "beginDate is null");
		checkArgument(datePeriod.isValid());
		checkNotNull(pageable);

		return contServiceDataHWaterRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				datePeriod.getDateFrom(), datePeriod.getDateTo(), pageable);
	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataHWater selectLastData(long contZPointId) {
		checkArgument(contZPointId > 0);
		List<ContServiceDataHWater> resultList = contServiceDataHWaterRepository.selectLastDataByZPoint(contZPointId,
				LIMIT1_PAGE_REQUEST);
		return resultList.size() > 0 ? resultList.get(0) : null;
	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Date selectLastDataDate(long contZPointId, Date fromDateTime) {
		checkArgument(contZPointId > 0);

		Date actialFromDate = fromDateTime;
		if (actialFromDate == null) {
			actialFromDate = JodaTimeUtils.startOfDay(DateTime.now().minusDays(3)).toDate();
		} else {
			logger.debug("MinCheck: {}", actialFromDate);
		}

		List<ContServiceDataHWater> resultList = contServiceDataHWaterRepository.selectLastDataByZPoint(contZPointId,
				actialFromDate, LIMIT1_PAGE_REQUEST);

		if (resultList.size() == 0) {
			resultList = contServiceDataHWaterRepository.selectLastDataByZPoint(contZPointId, LIMIT1_PAGE_REQUEST);
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Date selectLastDataDate(long contZPointId) {
		return selectLastDataDate(contZPointId, null);
	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Date selectAnyDataDate(long contZPointId) {
		checkArgument(contZPointId > 0);
		List<ContServiceDataHWater> resultList = contServiceDataHWaterRepository.selectAnyDataByZPoint(contZPointId,
				LIMIT1_PAGE_REQUEST);
		return resultList.size() > 0 ? resultList.get(0).getDataDate() : null;
	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Boolean selectExistsAnyData(long contZPointId) {
		checkArgument(contZPointId > 0);
		List<Long> resultList = contServiceDataHWaterRepository.selectExistsAnyDataByZPoint(contZPointId,
				LIMIT1_PAGE_REQUEST);
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataHWaterTotals selectContZPoint_Totals(long contZPointId, TimeDetailKey timeDetail,
			LocalDateTime beginDate, LocalDateTime endDate) {

		checkNotNull(timeDetail);
		checkNotNull(beginDate);
		checkNotNull(endDate);
		checkArgument(beginDate.compareTo(endDate) <= 0);

		logger.debug("selectContZPointTotals: contZPoint:{}; timeDetailType:{}; beginDate:{}; endDate:{}", contZPointId,
				timeDetail.getKeyname(), beginDate.toDate(), endDate.toDate());

		Query q1 = em.createQuery("SELECT sum(m_in) as m_in, sum(m_out) as m_out, sum(m_delta) as m_delta, "
				+ " sum(h_in) as h_in, sum(h_out) as h_out, sum(h_delta) as h_delta, "
				+ " sum(v_in) as v_in, sum(v_out) as v_out, sum(v_delta) as v_delta "
				+ " FROM ContServiceDataHWater hw " + " WHERE hw.timeDetailType = :timeDetailType "
				+ " AND hw.contZPoint.id = :contZPointId " + " AND hw.dataDate >= :beginDate "
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

	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataHWater selectContZPoint_Avgs(long contZPointId, TimeDetailKey timeDetail,
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
		sqlString.append(" AND hw.contZPoint.id = :contZPointId ");
		sqlString.append(" AND hw.dataDate >= :beginDate ");
		sqlString.append(" AND hw.dataDate <= :endDate ");
		logger.debug("Sql: {}", sqlString.toString());

		Query q1 = em.createQuery(sqlString.toString());

		q1.setParameter("timeDetailType", timeDetail.getKeyname());
		q1.setParameter("contZPointId", contZPointId);
		q1.setParameter("beginDate", period.getDateFrom());
		q1.setParameter("endDate", period.getDateTo());

		Object[] results = (Object[]) q1.getSingleResult();
		checkNotNull(results);

		ContServiceDataHWater result = new ContServiceDataHWater();
		result.setT_in(columnHelper.getResult(results, "t_in"));
		result.setT_out(columnHelper.getResult(results, "t_out"));
		result.setT_cold(columnHelper.getResult(results, "t_cold"));
		result.setT_outdoor(columnHelper.getResult(results, "t_outdoor"));
		result.setM_in(columnHelper.getResult(results, "m_in"));
		result.setM_out(columnHelper.getResult(results, "m_out"));
		result.setM_delta(columnHelper.getResult(results, "m_delta"));
		result.setV_in(columnHelper.getResult(results, "v_in"));
		result.setV_out(columnHelper.getResult(results, "v_out"));
		result.setV_delta(columnHelper.getResult(results, "v_delta"));
		result.setH_in(columnHelper.getResult(results, "h_in"));
		result.setH_out(columnHelper.getResult(results, "h_out"));
		result.setH_delta(columnHelper.getResult(results, "h_delta"));
		result.setP_in(columnHelper.getResult(results, "p_in"));
		result.setP_out(columnHelper.getResult(results, "p_out"));
		result.setP_delta(columnHelper.getResult(results, "p_delta"));
		result.setWorkTime(columnHelper.getResult(results, "workTime"));
		result.setFailTime(columnHelper.getResult(results, "failTime"));
		logger.info("value: {}", result.getT_in());

		return result;

	}

	/**
	 * 
	 * @param contZPointId
	 * @param localDateTime
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	private ContServiceDataHWater selectLastAbsData(long contZPointId, LocalDateTime localDateTime) {

		checkNotNull(localDateTime);

		String[] timeDetails = { // timeDetail.getAbsPair()
				TimeDetailKey.TYPE_1H.getAbsPair(), TimeDetailKey.TYPE_24H.getAbsPair() };

		List<ContServiceDataHWater> dataList = contServiceDataHWaterRepository
				.selectLastDetailDataByZPoint(contZPointId, timeDetails, localDateTime.toDate(), LIMIT1_PAGE_REQUEST);

		return dataList.size() > 0 ? dataList.get(0) : null;
	}

	/**
	 * 
	 * @param contZPointId
	 * @param localDateTime
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataHWater selectLastAbsData(long contZPointId, TimeDetailKey timeDetail,
			LocalDateTime localDateTime, boolean isEndDate) {

		checkNotNull(localDateTime);
		checkNotNull(timeDetail);

		String[] dataTimeDetails = { timeDetail.getKeyname() };

		Date dataDateLimit;

		if (isEndDate) {
			List<ContServiceDataHWater> dataList = contServiceDataHWaterRepository.selectLastDetailDataByZPoint(
					contZPointId, dataTimeDetails, localDateTime.toDate(), LIMIT1_PAGE_REQUEST);

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

		List<ContServiceDataHWater> integratorList = contServiceDataHWaterRepository
				.selectLastDetailDataByZPoint(contZPointId, integratorTimeDetails, dataDateLimit, LIMIT1_PAGE_REQUEST);

		return integratorList.size() > 0 ? integratorList.get(0) : null;
	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataHWaterAbs_Csv> selectDataAbs_Csv(long contZPointId, TimeDetailKey timeDetail,
			DateTime beginDate, DateTime endDate) {

		List<ContServiceDataHWater> srcDataList = selectByContZPoint(contZPointId, timeDetail, beginDate, endDate);

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
	 * @param contZPointId
	 * @param inData
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	public void insertManualLoadDataHWater(Long contZPointId, List<ContServiceDataHWater> inData, File outFile) {

		checkNotNull(contZPointId);
		checkNotNull(inData);
		checkArgument(inData.size() > 0);

		ContZPoint zpoint = contZPointService.findOne(contZPointId);

		checkNotNull(zpoint, String.format("ContZPoint with id:%d is not found", contZPointId));

		checkState(BooleanUtils.isTrue(zpoint.getIsManualLoading()),
				String.format("Manual Loading for ContZPoint with id:%d is not allowed", contZPointId));

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
			d.setContZPointId(contZPointId);
			d.setTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname());
			d.setDeviceObject(dObject);
		});

		contServiceDataHWaterRepository.save(inData);

	}

	/**
	 * 
	 * @param contZPointId
	 * @param localDatePeriod
	 * @param outFile
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	public List<ContServiceDataHWater> deleteManualDataHWater(Long contZPointId, LocalDatePeriod localDatePeriod,
			File outFile) {

		checkNotNull(contZPointId);

		checkNotNull(localDatePeriod);

		ContZPoint zpoint = contZPointService.findOne(contZPointId);

		checkNotNull(zpoint, String.format("ContZPoint with id:%d is not found", contZPointId));

		checkState(BooleanUtils.isTrue(zpoint.getIsManualLoading()),
				String.format("Manual Loading and Deleting for ContZPoint with id:%d is not allowed", contZPointId));

		List<ContServiceDataHWater> deleteCandidate = selectByContZPoint(contZPointId, TimeDetailKey.TYPE_24H,
				localDatePeriod);

		try {
			ByteArrayInputStream is = new ByteArrayInputStream(hWatersCsvService.writeHWaterDataToCsv(deleteCandidate));

			@SuppressWarnings("unused")
			String digestMD5 = FileWriterUtils.writeFile(is, outFile);

		} catch (IOException e) {
			throw new PersistenceException(
					String.format("Can't save into file (%s) cadidate for delete rows for contZPointId=%d",
							outFile.getAbsolutePath(), contZPointId));
		}

		contServiceDataHWaterRepository.delete(deleteCandidate);

		return deleteCandidate;
	}

}
