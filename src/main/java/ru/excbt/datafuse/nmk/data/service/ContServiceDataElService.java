package ru.excbt.datafuse.nmk.data.service;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
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
import ru.excbt.datafuse.nmk.data.model.support.TimeDetailLastDate;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElConsRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElProfileRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElTechRepository;
import ru.excbt.datafuse.nmk.service.utils.ColumnHelper;
import ru.excbt.datafuse.nmk.utils.DateInterval;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Сервис по работе с данными по электроснабжению
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2015
 *
 */
@Service
public class ContServiceDataElService {

	private static final Logger logger = LoggerFactory.getLogger(ContServiceDataElService.class);

    public static final TemporalAmount LAST_DATA_DATE_DEPTH_DURATION = Duration.ofDays(30);

    public static final TemporalAmount MAX_LAST_DATA_DATE_DEPTH_DURATION = Duration.ofDays(365);

	private static final PageRequest LIMIT1_PAGE_REQUEST = new PageRequest(0, 1);

	private static final String[] CONS_COLUMNS = new String[] { "p_Ap1", "p_An1", "q_Rp1", "q_Rn1", "p_Ap2", "p_An2",
			"q_Rp2", "q_Rn2", "p_Ap3", "p_An3", "q_Rp3", "q_Rn3", "p_Ap4", "p_An4", "q_Rp4", "q_Rn4", "p_Ap", "p_An",
			"q_Rp", "q_Rn" };

	private static final Set<String> EL_SERVICE_TYPE_SET = ImmutableSet.of(ContServiceTypeKey.EL.getKeyname());

	private final ContServiceDataElConsRepository contServiceDataElConsRepository;

	private final ContServiceDataElTechRepository contServiceDataElTechRepository;

	private final ContServiceDataElProfileRepository contServiceDataElProfileRepository;

	private final DBSessionService dbSessionService;

	@Autowired
    public ContServiceDataElService(ContServiceDataElConsRepository contServiceDataElConsRepository,
                                    ContServiceDataElTechRepository contServiceDataElTechRepository,
                                    ContServiceDataElProfileRepository contServiceDataElProfileRepository, DBSessionService dbSessionService) {
        this.contServiceDataElConsRepository = contServiceDataElConsRepository;
        this.contServiceDataElTechRepository = contServiceDataElTechRepository;
        this.contServiceDataElProfileRepository = contServiceDataElProfileRepository;
        this.dbSessionService = dbSessionService;
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
			DateInterval localDatePeriod) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		return contServiceDataElConsRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				localDatePeriod.getFromDate(), localDatePeriod.getToDate());

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

		// Totals
		ContServiceDataElCons totals = selectCons_Sum(contZPointId, timeDetail, localDatePeriod);
		totals.setTimeDetailType(timeDetail.getKeyname());
		totals.setContZPointId(contZPointId);

		// First @ Last Data Data
		ContServiceDataElCons firstData = ContServiceDataTool.getFirstElement(contServiceDataElConsRepository.selectFirstDataByZPoint(
				contZPointId, timeDetailTypes, localDatePeriod.getDateFrom(), LIMIT1_PAGE_REQUEST));

		ContServiceDataElCons lastData = ContServiceDataTool.getFirstElement(contServiceDataElConsRepository.selectLastDataByZPoint(
				contZPointId, timeDetailTypes, localDatePeriod.getDateTo(), LIMIT1_PAGE_REQUEST));

		// Abs values
		ContServiceDataElCons diffsAbs = null;

		if (timeDetail.isHaveAbs()) {

			String[] timeDetailAbsTypes = new String[] { timeDetail.getAbsPair() };

			ContServiceDataElCons firstDataAbs = ContServiceDataTool.getFirstElement(
					contServiceDataElConsRepository.selectFirstDataByZPoint(contZPointId, timeDetailAbsTypes,
							localDatePeriod.getDateFrom(), LIMIT1_PAGE_REQUEST));

			ContServiceDataElCons lastDataAbs = ContServiceDataTool.getFirstElement(
					contServiceDataElConsRepository.selectLastDataByZPoint(contZPointId, timeDetailAbsTypes,
							localDatePeriod.buildDateToNextDay().getDateTo(), LIMIT1_PAGE_REQUEST));

			if (firstDataAbs != null && lastDataAbs != null) {

				diffsAbs = new ContServiceDataElCons();

				diffsAbs.setContZPointId(contZPointId);
				diffsAbs.setTimeDetailType(timeDetail.getAbsPair());

				processDiffs(diffsAbs, firstDataAbs, lastDataAbs);

			}

			summary.setFirstDataAbs(firstDataAbs);
			summary.setLastDataAbs(lastDataAbs);

		}

		summary.setTotals(totals);
		summary.setFirstData(firstData);
		summary.setLastData(lastData);
		summary.setDiffsAbs(diffsAbs);

		return summary;

	}

	/**
	 *
	 * @param diffs
	 * @param firstData
	 * @param lastData
	 */
	private void processDiffs(ContServiceDataElCons diffs, ContServiceDataElCons firstData,
			ContServiceDataElCons lastData) {
		diffs.setP_Ap1(ContServiceDataTool.processDelta(firstData.getP_Ap1(), lastData.getP_Ap1()));
		diffs.setP_An1(ContServiceDataTool.processDelta(firstData.getP_An1(), lastData.getP_An1()));
		diffs.setQ_Rp1(ContServiceDataTool.processDelta(firstData.getQ_Rp1(), lastData.getQ_Rp1()));
		diffs.setQ_Rn1(ContServiceDataTool.processDelta(firstData.getQ_Rn1(), lastData.getQ_Rn1()));
		diffs.setP_Ap2(ContServiceDataTool.processDelta(firstData.getP_Ap2(), lastData.getP_Ap2()));
		diffs.setP_An2(ContServiceDataTool.processDelta(firstData.getP_An2(), lastData.getP_An2()));
		diffs.setQ_Rp2(ContServiceDataTool.processDelta(firstData.getQ_Rp2(), lastData.getQ_Rp2()));
		diffs.setQ_Rn2(ContServiceDataTool.processDelta(firstData.getQ_Rn2(), lastData.getQ_Rn2()));
		diffs.setP_Ap3(ContServiceDataTool.processDelta(firstData.getP_Ap3(), lastData.getP_Ap3()));
		diffs.setP_An3(ContServiceDataTool.processDelta(firstData.getP_An3(), lastData.getP_An3()));
		diffs.setQ_Rp3(ContServiceDataTool.processDelta(firstData.getQ_Rp3(), lastData.getQ_Rp3()));
		diffs.setQ_Rn3(ContServiceDataTool.processDelta(firstData.getQ_Rn3(), lastData.getQ_Rn3()));
		diffs.setP_Ap4(ContServiceDataTool.processDelta(firstData.getP_Ap4(), lastData.getP_Ap4()));
		diffs.setP_An4(ContServiceDataTool.processDelta(firstData.getP_An4(), lastData.getP_An4()));
		diffs.setQ_Rp4(ContServiceDataTool.processDelta(firstData.getQ_Rp4(), lastData.getQ_Rp4()));
		diffs.setQ_Rn4(ContServiceDataTool.processDelta(firstData.getQ_Rn4(), lastData.getQ_Rn4()));
		diffs.setP_Ap(ContServiceDataTool.processDelta(firstData.getP_Ap(), lastData.getP_Ap()));
		diffs.setP_An(ContServiceDataTool.processDelta(firstData.getP_An(), lastData.getP_An()));
		diffs.setQ_Rp(ContServiceDataTool.processDelta(firstData.getQ_Rp(), lastData.getQ_Rp()));
		diffs.setQ_Rn(ContServiceDataTool.processDelta(firstData.getQ_Rn(), lastData.getQ_Rn()));
	}

	/**
	 *
	 * @param columnHelper
	 * @param queryResults
	 * @return
	 */
	private ContServiceDataElCons consColumnHelperReader(ColumnHelper columnHelper, Object[] queryResults) {
		ContServiceDataElCons result = new ContServiceDataElCons();

		result.setP_Ap1(columnHelper.getResultDouble(queryResults, "p_Ap1"));
		result.setP_An1(columnHelper.getResultDouble(queryResults, "p_An1"));
		result.setQ_Rp1(columnHelper.getResultDouble(queryResults, "q_Rp1"));
		result.setQ_Rn1(columnHelper.getResultDouble(queryResults, "q_Rn1"));

		result.setP_Ap2(columnHelper.getResultDouble(queryResults, "p_Ap2"));
		result.setP_An2(columnHelper.getResultDouble(queryResults, "p_An2"));
		result.setQ_Rp2(columnHelper.getResultDouble(queryResults, "q_Rp2"));
		result.setQ_Rn2(columnHelper.getResultDouble(queryResults, "q_Rn2"));

		result.setP_Ap3(columnHelper.getResultDouble(queryResults, "p_Ap3"));
		result.setP_An3(columnHelper.getResultDouble(queryResults, "p_An3"));
		result.setQ_Rp3(columnHelper.getResultDouble(queryResults, "q_Rp3"));
		result.setQ_Rn3(columnHelper.getResultDouble(queryResults, "q_Rn3"));

		result.setP_Ap4(columnHelper.getResultDouble(queryResults, "p_Ap4"));
		result.setP_An4(columnHelper.getResultDouble(queryResults, "p_An4"));
		result.setQ_Rp4(columnHelper.getResultDouble(queryResults, "q_Rp4"));
		result.setQ_Rn4(columnHelper.getResultDouble(queryResults, "q_Rn4"));

		result.setP_Ap(columnHelper.getResultDouble(queryResults, "p_Ap"));
		result.setP_An(columnHelper.getResultDouble(queryResults, "p_An"));
		result.setQ_Rp(columnHelper.getResultDouble(queryResults, "q_Rp"));
		result.setQ_Rn(columnHelper.getResultDouble(queryResults, "q_Rn"));

		return result;
	}

    /**
     *
     * @param contZPointId
     * @param timeDetail
     * @param localDatePeriod
     * @return
     */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContServiceDataElCons selectCons_Avg(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {

		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq());

		ColumnHelper columnHelper = new ColumnHelper(CONS_COLUMNS, "avg(%s)");

		Object[] queryResults = ContServiceDataTool.serviceDataCustomQuery(contZPointId, timeDetail, localDatePeriod, columnHelper,
				ContServiceDataElCons.class, dbSessionService.getSession());

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
	public ContServiceDataElCons selectCons_Min(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {

		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq());

		ColumnHelper columnHelper = new ColumnHelper(CONS_COLUMNS, "min(%s)");

		Object[] queryResults = ContServiceDataTool.serviceDataCustomQuery(contZPointId, timeDetail, localDatePeriod, columnHelper,
				ContServiceDataElCons.class, dbSessionService.getSession());

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
	public ContServiceDataElCons selectCons_Max(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {

		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq());

		ColumnHelper columnHelper = new ColumnHelper(CONS_COLUMNS, "max(%s)");

		Object[] queryResults = ContServiceDataTool.serviceDataCustomQuery(contZPointId, timeDetail, localDatePeriod, columnHelper,
				ContServiceDataElCons.class, dbSessionService.getSession());

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

		Object[] queryResults = ContServiceDataTool.serviceDataCustomQuery(contZPointId, timeDetail, localDatePeriod, columnHelper,
				ContServiceDataElCons.class, dbSessionService.getSession());

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

		ContServiceDataElTech firstData = ContServiceDataTool.getFirstElement(contServiceDataElTechRepository.selectFirstDataByZPoint(
				contZPointId, timeDetailTypes, localDatePeriod.getDateFrom(), LIMIT1_PAGE_REQUEST));

		ContServiceDataElTech lastData = ContServiceDataTool.getFirstElement(contServiceDataElTechRepository.selectLastDataByZPoint(
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

		Object[] queryResults = ContServiceDataTool.serviceDataCustomQuery(contZPointId, timeDetail, localDatePeriod, columnHelper,
				ContServiceDataElTech.class, dbSessionService.getSession());

		ContServiceDataElTech result = new ContServiceDataElTech();
		result.setU1(columnHelper.getResultDouble(queryResults, "u1"));
		result.setU2(columnHelper.getResultDouble(queryResults, "u2"));
		result.setU3(columnHelper.getResultDouble(queryResults, "u3"));

		result.setI1(columnHelper.getResultDouble(queryResults, "i1"));
		result.setI2(columnHelper.getResultDouble(queryResults, "i2"));
		result.setI3(columnHelper.getResultDouble(queryResults, "i3"));

		result.setK1(columnHelper.getResultDouble(queryResults, "k1"));
		result.setK2(columnHelper.getResultDouble(queryResults, "k2"));
		result.setK3(columnHelper.getResultDouble(queryResults, "k3"));

		return result;

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

		ContServiceDataElProfile firstData = ContServiceDataTool.getFirstElement(contServiceDataElProfileRepository.selectFirstDataByZPoint(
				contZPointId, timeDetailTypes, localDatePeriod.getDateFrom(), LIMIT1_PAGE_REQUEST));

		ContServiceDataElProfile lastData = ContServiceDataTool.getFirstElement(contServiceDataElProfileRepository.selectLastDataByZPoint(
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
     * @param localDatePeriod
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

		Object[] queryResults = ContServiceDataTool.serviceDataCustomQuery(contZPointId, timeDetail, localDatePeriod, columnHelper,
				ContServiceDataElProfile.class, dbSessionService.getSession());

		ContServiceDataElProfile result = new ContServiceDataElProfile();
		result.setP_Ap(columnHelper.getResultDouble(queryResults, "p_Ap"));
		result.setP_An(columnHelper.getResultDouble(queryResults, "p_An"));
		result.setQ_Rp(columnHelper.getResultDouble(queryResults, "q_Rp"));
		result.setQ_Rn(columnHelper.getResultDouble(queryResults, "q_Rn"));

		return result;

	}

	/**
	 *
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Boolean selectExistsAnyConsData(long contZPointId) {
		checkArgument(contZPointId > 0);
		List<Long> resultList = contServiceDataElConsRepository.selectExistsAnyDataByZPoint(contZPointId,
				LIMIT1_PAGE_REQUEST);
		return resultList.size() > 0;
	}

	/**
	 *
	 * @param contZPointId
	 * @param fromDateTime
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public LocalDateTime selectLastConsDataDate(long contZPointId, LocalDateTime fromDateTime) {
		checkArgument(contZPointId > 0);

		LocalDateTime actialFromDate = fromDateTime;
		if (actialFromDate == null) {
            actialFromDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minus(LAST_DATA_DATE_DEPTH_DURATION);
		} else {
			logger.debug("MinCheck: {}", actialFromDate);
		}

		List<Timestamp> resultList = contServiceDataElConsRepository.selectLastDataDateByZPointMax(contZPointId,
				LocalDateUtils.asDate(actialFromDate));

//        if (resultList.get(0) == null) {
//			resultList = contServiceDataElConsRepository.selectLastDataDateByZPointMax(contZPointId,
//                LocalDateUtils.asDate(actialFromDate.minus(MAX_LAST_DATA_DATE_DEPTH_DURATION)));
//		}

		return resultList.get(0) != null ? resultList.get(0).toLocalDateTime() : null;
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

		List<Long> contZPointIds = idServiceTypePairs.stream().filter(i -> EL_SERVICE_TYPE_SET.contains(i.getLeft()))
				.map(i -> i.getRight()).collect(Collectors.toList());

		if (contZPointIds.isEmpty()) {
		    return new HashMap<>();
        } else
            return
                ContServiceDataUtil.collectContZPointTimeDetailTypes(
                    contServiceDataElConsRepository.selectTimeDetailLastDataByZPoint(contZPointIds));
	}

}
