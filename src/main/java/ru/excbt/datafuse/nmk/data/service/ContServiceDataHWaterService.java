package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.constant.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterCsv;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterTotals;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataHWaterRepository;

@Service
@Transactional
public class ContServiceDataHWaterService {

	private static final Logger logger = LoggerFactory
			.getLogger(ContServiceDataHWaterService.class);

	private static final PageRequest LIMIT1_PAGE_REQUEST = new PageRequest(0, 1);

	@Autowired
	private ContServiceDataHWaterRepository contServiceDataHWaterRepository;

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
	public List<ContServiceDataHWater> selectByContZPoint(long contZPointId,
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
	public Date selectLastDataDate(long contZPointId) {
		checkArgument(contZPointId > 0);
		List<ContServiceDataHWater> resultList = contServiceDataHWaterRepository
				.selectLastDataByZPoint(contZPointId, LIMIT1_PAGE_REQUEST);
		return resultList.size() > 0 ? resultList.get(0).getDataDate() : null;
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
//	@Transactional(readOnly = true)
//	private ContServiceDataHWater selectLastAbsData(long contZPointId,
//			LocalDateTime localDateTime) {
//
//		checkNotNull(localDateTime);
//
//		String[] timeDetails = { TimeDetailKey.TYPE_ABS.getKeyname() };
//
//		List<ContServiceDataHWater> dataList = contServiceDataHWaterRepository
//				.selectLastDataByZPoint(contZPointId, timeDetails,
//						localDateTime.toDate(), LIMIT1_PAGE_REQUEST);
//
//		return dataList.size() > 0 ? dataList.get(0) : null;
//	}

	/**
	 * 
	 * @param contZPointId
	 * @param localDateTime
	 * @return
	 */
	@Transactional(readOnly = true)
	public ContServiceDataHWater selectLastAbsData(long contZPointId,
			TimeDetailKey timeDetail, LocalDateTime localDateTime) {

		checkNotNull(localDateTime);
		checkNotNull(timeDetail);

		String[] timeDetails = { //TimeDetailKey.TYPE_ABS.getKeyname(),
				timeDetail.getAbsPair() };

		List<ContServiceDataHWater> dataList = contServiceDataHWaterRepository
				.selectLastDataByZPoint(contZPointId, timeDetails,
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
	public List<ContServiceDataHWaterCsv> selectByContZPointCsvData(
			long contZPointId, TimeDetailKey timeDetail, DateTime beginDate,
			DateTime endDate) {

		List<ContServiceDataHWater> srcDataList = selectByContZPoint(
				contZPointId, timeDetail, beginDate, endDate);

		List<ContServiceDataHWaterCsv> cvsDataList = new ArrayList<>();
		try {

			for (ContServiceDataHWater data : srcDataList) {
				ContServiceDataHWaterCsv cvsData;
				cvsData = ContServiceDataHWaterCsv.newInstance(data);
				ContServiceDataHWater abs = selectLastAbsData(
						data.getContZPointId(), timeDetail, new LocalDateTime(
								data.getDataDate()));
				cvsData.copyAbsData(abs);
				cvsDataList.add(cvsData);
			}

		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("Can't create intance of {}: {}",
					ContServiceDataHWaterCsv.class, e);
			cvsDataList.clear();
		}

		return cvsDataList;
	}

}
