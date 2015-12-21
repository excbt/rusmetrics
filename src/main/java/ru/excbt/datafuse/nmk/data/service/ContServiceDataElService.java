package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataElCons;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataElProfile;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataElTech;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataSummary;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElConsRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElProfileRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElTechRepository;
import ru.excbt.datafuse.nmk.data.service.support.ColumnHelper;

@Service
public class ContServiceDataElService extends AbstractContServiceDataService {

	private static final Logger logger = LoggerFactory.getLogger(ContServiceDataElService.class);

	private static final PageRequest LIMIT1_PAGE_REQUEST = new PageRequest(0, 1);

	private static final String[] CONS_COLUMNS = new String[] { "p_Ap1", "p_An1", "q_Rp1", "q_Rn1", "p_Ap2", "p_An2",
			"q_Rp2", "q_Rn2", "p_Ap3", "p_An3", "q_Rp3", "q_Rn3", "p_Ap4", "p_An4", "q_Rp4", "q_Rn4", "p_Ap", "p_An",
			"q_Rp", "q_Rn" };

	@Autowired
	private ContServiceDataElConsRepository contServiceDataElConsRepository;

	@Autowired
	private ContServiceDataElTechRepository contServiceDataElTechRepository;

	@Autowired
	private ContServiceDataElProfileRepository contServiceDataElProfileRepository;

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 * @param pageRequest
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Page<ContServiceDataElCons> selectConsByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		return contServiceDataElConsRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				localDatePeriod.getDateFrom(), localDatePeriod.getDateTo(), pageRequest);

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataElCons> selectConsByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		return contServiceDataElConsRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				localDatePeriod.getDateFrom(), localDatePeriod.getDateTo());

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataSummary<ContServiceDataElCons> selectConsSummary(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {

		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		String[] timeDetailTypes = new String[] { timeDetail.getKeyname() };

		ContServiceDataSummary<ContServiceDataElCons> summary = new ContServiceDataSummary<>();

		ContServiceDataElCons totals = selectCons_Sum(contZPointId, timeDetail, localDatePeriod);

		ContServiceDataElCons firstData = getFirstElement(contServiceDataElConsRepository.selectFirstDataByZPoint(
				contZPointId, timeDetailTypes, localDatePeriod.getDateFrom(), LIMIT1_PAGE_REQUEST));

		ContServiceDataElCons lastData = getFirstElement(contServiceDataElConsRepository.selectLastDataByZPoint(
				contZPointId, timeDetailTypes, localDatePeriod.getDateTo(), LIMIT1_PAGE_REQUEST));

		ContServiceDataElCons avg = null;

		ContServiceDataElCons diffs = null;

		summary.setTotals(totals);
		summary.setFirstData(firstData);
		summary.setLastData(lastData);
		summary.setAverage(avg);
		summary.setDiffs(diffs);

		return summary;

	}

	/**
	 * 
	 * @param columnHelper
	 * @param queryResults
	 * @return
	 */
	private ContServiceDataElCons consColumnHelperReader(ColumnHelper columnHelper, Object[] queryResults) {
		ContServiceDataElCons result = new ContServiceDataElCons();

		result.setP_Ap1(columnHelper.getResult(queryResults, "p_Ap1"));
		result.setP_An1(columnHelper.getResult(queryResults, "p_An1"));
		result.setQ_Rp1(columnHelper.getResult(queryResults, "q_Rp1"));
		result.setQ_Rn1(columnHelper.getResult(queryResults, "q_Rn1"));

		result.setP_Ap2(columnHelper.getResult(queryResults, "p_Ap2"));
		result.setP_An2(columnHelper.getResult(queryResults, "p_An2"));
		result.setQ_Rp2(columnHelper.getResult(queryResults, "q_Rp2"));
		result.setQ_Rn2(columnHelper.getResult(queryResults, "q_Rn2"));

		result.setP_Ap3(columnHelper.getResult(queryResults, "p_Ap3"));
		result.setP_An3(columnHelper.getResult(queryResults, "p_An3"));
		result.setQ_Rp3(columnHelper.getResult(queryResults, "q_Rp3"));
		result.setQ_Rn3(columnHelper.getResult(queryResults, "q_Rn3"));

		result.setP_Ap4(columnHelper.getResult(queryResults, "p_Ap4"));
		result.setP_An4(columnHelper.getResult(queryResults, "p_An4"));
		result.setQ_Rp4(columnHelper.getResult(queryResults, "q_Rp4"));
		result.setQ_Rn4(columnHelper.getResult(queryResults, "q_Rn4"));

		result.setP_Ap(columnHelper.getResult(queryResults, "p_Ap"));
		result.setP_An(columnHelper.getResult(queryResults, "p_An"));
		result.setQ_Rp(columnHelper.getResult(queryResults, "q_Rp"));
		result.setQ_Rn(columnHelper.getResult(queryResults, "q_Rn"));

		return result;
	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param period
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataElCons selectCons_Avg(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {

		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq());

		ColumnHelper columnHelper = new ColumnHelper(CONS_COLUMNS, "avg(%s)");

		Object[] queryResults = serviceDataCustomQuery(contZPointId, timeDetail, localDatePeriod, columnHelper,
				ContServiceDataElCons.class);

		return consColumnHelperReader(columnHelper, queryResults);

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataElCons selectCons_Sum(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {

		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq());

		ColumnHelper columnHelper = new ColumnHelper(CONS_COLUMNS, "sum(%s)");

		Object[] queryResults = serviceDataCustomQuery(contZPointId, timeDetail, localDatePeriod, columnHelper,
				ContServiceDataElCons.class);

		return consColumnHelperReader(columnHelper, queryResults);

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 * @param pageRequest
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Page<ContServiceDataElTech> selectTechByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		return contServiceDataElTechRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				localDatePeriod.getDateFrom(), localDatePeriod.getDateTo(), pageRequest);

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataElTech> selectTechByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		return contServiceDataElTechRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				localDatePeriod.getDateFrom(), localDatePeriod.getDateTo());

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataSummary<ContServiceDataElTech> selectTechSummary(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {

		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		String[] timeDetailTypes = new String[] { timeDetail.getKeyname() };

		ContServiceDataSummary<ContServiceDataElTech> summary = new ContServiceDataSummary<>();

		ContServiceDataElTech totals = null;

		ContServiceDataElTech firstData = getFirstElement(contServiceDataElTechRepository.selectFirstDataByZPoint(
				contZPointId, timeDetailTypes, localDatePeriod.getDateFrom(), LIMIT1_PAGE_REQUEST));

		ContServiceDataElTech lastData = getFirstElement(contServiceDataElTechRepository.selectLastDataByZPoint(
				contZPointId, timeDetailTypes, localDatePeriod.getDateTo(), LIMIT1_PAGE_REQUEST));

		ContServiceDataElTech avg = selectTech_Avg(contZPointId, timeDetail, localDatePeriod);

		ContServiceDataElTech diffs = null;

		summary.setTotals(totals);
		summary.setFirstData(firstData);
		summary.setLastData(lastData);
		summary.setAverage(avg);
		summary.setDiffs(diffs);

		return summary;

	}

	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataElTech selectTech_Avg(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {

		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq());

		String[] columns = new String[] { "u1", "u2", "u3", "i1", "i2", "i3", "k1", "k2", "k3" };

		ColumnHelper columnHelper = new ColumnHelper(columns, "avg(%s)");

		Object[] queryResults = serviceDataCustomQuery(contZPointId, timeDetail, localDatePeriod, columnHelper,
				ContServiceDataElTech.class);

		ContServiceDataElTech result = new ContServiceDataElTech();
		result.setU1(columnHelper.getResult(queryResults, "u1"));
		result.setU2(columnHelper.getResult(queryResults, "u2"));
		result.setU3(columnHelper.getResult(queryResults, "u3"));

		result.setI1(columnHelper.getResult(queryResults, "i1"));
		result.setI2(columnHelper.getResult(queryResults, "i2"));
		result.setI3(columnHelper.getResult(queryResults, "i3"));

		result.setK1(columnHelper.getResult(queryResults, "k1"));
		result.setK2(columnHelper.getResult(queryResults, "k2"));
		result.setK3(columnHelper.getResult(queryResults, "k3"));

		return result;

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param beginDate
	 * @param endDate
	 * @param pageRequest
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Page<ContServiceDataElProfile> selectProfileByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		return contServiceDataElProfileRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				localDatePeriod.getDateFrom(), localDatePeriod.getDateTo(), pageRequest);

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataElProfile> selectProfileByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		return contServiceDataElProfileRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				localDatePeriod.getDateFrom(), localDatePeriod.getDateTo());

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataSummary<ContServiceDataElProfile> selectProfileSummary(Long contZPointId,
			TimeDetailKey timeDetail, LocalDatePeriod localDatePeriod) {

		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		String[] timeDetailTypes = new String[] { timeDetail.getKeyname() };

		ContServiceDataSummary<ContServiceDataElProfile> summary = new ContServiceDataSummary<>();

		ContServiceDataElProfile totals = null;

		ContServiceDataElProfile firstData = getFirstElement(contServiceDataElProfileRepository.selectFirstDataByZPoint(
				contZPointId, timeDetailTypes, localDatePeriod.getDateFrom(), LIMIT1_PAGE_REQUEST));

		ContServiceDataElProfile lastData = getFirstElement(contServiceDataElProfileRepository.selectLastDataByZPoint(
				contZPointId, timeDetailTypes, localDatePeriod.getDateTo(), LIMIT1_PAGE_REQUEST));

		ContServiceDataElProfile avg = selectProfile_Avg(contZPointId, timeDetail, localDatePeriod);

		ContServiceDataElProfile diffs = null;

		summary.setTotals(totals);
		summary.setFirstData(firstData);
		summary.setLastData(lastData);
		summary.setAverage(avg);
		summary.setDiffs(diffs);

		return summary;

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param period
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataElProfile selectProfile_Avg(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {

		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq());

		String[] columns = new String[] { "p_Ap", "p_An", "q_Rp", "q_Rn" };

		ColumnHelper columnHelper = new ColumnHelper(columns, "avg(%s)");

		Object[] queryResults = serviceDataCustomQuery(contZPointId, timeDetail, localDatePeriod, columnHelper,
				ContServiceDataElProfile.class);

		ContServiceDataElProfile result = new ContServiceDataElProfile();
		result.setP_Ap(columnHelper.getResult(queryResults, "p_Ap"));
		result.setP_An(columnHelper.getResult(queryResults, "p_An"));
		result.setQ_Rp(columnHelper.getResult(queryResults, "q_Rp"));
		result.setQ_Rn(columnHelper.getResult(queryResults, "q_Rn"));

		return result;

	}

}
