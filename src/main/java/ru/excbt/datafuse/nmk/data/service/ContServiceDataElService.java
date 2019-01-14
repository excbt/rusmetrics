package ru.excbt.datafuse.nmk.data.service;

import com.google.common.collect.ImmutableSet;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.NumberExpression;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataSummary;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.TimeDetailLastDate;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElConsRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElProfileRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElTechRepository;
import ru.excbt.datafuse.nmk.service.QueryDSLService;
import ru.excbt.datafuse.nmk.service.utils.ColumnHelper;
import ru.excbt.datafuse.nmk.utils.DateInterval;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
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

    private static final QContServiceDataElCons qContServiceDataElCons = QContServiceDataElCons.contServiceDataElCons;
    private static final QContServiceDataElProfile qContServiceDataElProfile = QContServiceDataElProfile.contServiceDataElProfile;
    private static final QContServiceDataElTech qContServiceDataElTech = QContServiceDataElTech.contServiceDataElTech;

    private static final List<NumberExpression<Double>> dataElConsColumnPathList =
        Arrays.asList(
            qContServiceDataElCons.p_Ap,
            qContServiceDataElCons.p_Ap1,
            qContServiceDataElCons.p_Ap2,
            qContServiceDataElCons.p_Ap3,
            qContServiceDataElCons.p_Ap4,

            qContServiceDataElCons.p_An,
            qContServiceDataElCons.p_An1,
            qContServiceDataElCons.p_An2,
            qContServiceDataElCons.p_An3,
            qContServiceDataElCons.p_An4,

            qContServiceDataElCons.q_Rp,
            qContServiceDataElCons.q_Rp1,
            qContServiceDataElCons.q_Rp2,
            qContServiceDataElCons.q_Rp3,
            qContServiceDataElCons.q_Rp4,

            qContServiceDataElCons.q_Rn,
            qContServiceDataElCons.q_Rn1,
            qContServiceDataElCons.q_Rn2,
            qContServiceDataElCons.q_Rn3,
            qContServiceDataElCons.q_Rn4);

    private static final List<NumberExpression<Double>> dataElProfileColumnPathList =
        Arrays.asList(
            qContServiceDataElProfile.p_Ap,
            qContServiceDataElProfile.p_An,
            qContServiceDataElProfile.q_Rp,
            qContServiceDataElProfile.q_Rn);

    private static final List<NumberExpression<Double>> dataElTechColumnPathList =
        Arrays.asList(
            qContServiceDataElTech.u1,
            qContServiceDataElTech.u2,
            qContServiceDataElTech.u3,
            qContServiceDataElTech.i1,
            qContServiceDataElTech.i2,
            qContServiceDataElTech.i3,
            qContServiceDataElTech.k1,
            qContServiceDataElTech.k2,
            qContServiceDataElTech.k3);

	private static final Set<String> EL_SERVICE_TYPE_SET = ImmutableSet.of(ContServiceTypeKey.EL.getKeyname());

	private final ContServiceDataElConsRepository contServiceDataElConsRepository;

	private final ContServiceDataElTechRepository contServiceDataElTechRepository;

	private final ContServiceDataElProfileRepository contServiceDataElProfileRepository;

	private final QueryDSLService queryDSLService;

	@Autowired
    public ContServiceDataElService(ContServiceDataElConsRepository contServiceDataElConsRepository,
                                    ContServiceDataElTechRepository contServiceDataElTechRepository,
                                    ContServiceDataElProfileRepository contServiceDataElProfileRepository, QueryDSLService queryDSLService) {
        this.contServiceDataElConsRepository = contServiceDataElConsRepository;
        this.contServiceDataElTechRepository = contServiceDataElTechRepository;
        this.contServiceDataElProfileRepository = contServiceDataElProfileRepository;
        this.queryDSLService = queryDSLService;
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


	private ContServiceDataElCons consColumnHelperReader(Tuple queryResults,
                                                         Function<NumberExpression<Double>, NumberExpression<Double>> mapper) {
		ContServiceDataElCons result = new ContServiceDataElCons();

		result.setP_Ap1(queryResults.get(mapper.apply(qContServiceDataElCons.p_Ap1)));
		result.setP_An1(queryResults.get(mapper.apply(qContServiceDataElCons.p_An1)));
		result.setQ_Rp1(queryResults.get(mapper.apply(qContServiceDataElCons.q_Rp1)));
		result.setQ_Rn1(queryResults.get(mapper.apply(qContServiceDataElCons.q_Rn1)));

        result.setP_Ap2(queryResults.get(mapper.apply(qContServiceDataElCons.p_Ap2)));
        result.setP_An2(queryResults.get(mapper.apply(qContServiceDataElCons.p_An2)));
        result.setQ_Rp2(queryResults.get(mapper.apply(qContServiceDataElCons.q_Rp2)));
        result.setQ_Rn2(queryResults.get(mapper.apply(qContServiceDataElCons.q_Rn2)));

        result.setP_Ap3(queryResults.get(mapper.apply(qContServiceDataElCons.p_Ap3)));
        result.setP_An3(queryResults.get(mapper.apply(qContServiceDataElCons.p_An3)));
        result.setQ_Rp3(queryResults.get(mapper.apply(qContServiceDataElCons.q_Rp3)));
        result.setQ_Rn3(queryResults.get(mapper.apply(qContServiceDataElCons.q_Rn3)));

        result.setP_Ap4(queryResults.get(mapper.apply(qContServiceDataElCons.p_Ap4)));
        result.setP_An4(queryResults.get(mapper.apply(qContServiceDataElCons.p_An4)));
        result.setQ_Rp4(queryResults.get(mapper.apply(qContServiceDataElCons.q_Rp4)));
        result.setQ_Rn4(queryResults.get(mapper.apply(qContServiceDataElCons.q_Rn4)));

        result.setP_Ap5(queryResults.get(mapper.apply(qContServiceDataElCons.p_Ap5)));
        result.setP_An5(queryResults.get(mapper.apply(qContServiceDataElCons.p_An5)));
        result.setQ_Rp5(queryResults.get(mapper.apply(qContServiceDataElCons.q_Rp5)));
        result.setQ_Rn5(queryResults.get(mapper.apply(qContServiceDataElCons.q_Rn5)));

        result.setP_Ap(queryResults.get(mapper.apply(qContServiceDataElCons.p_Ap)));
        result.setP_An(queryResults.get(mapper.apply(qContServiceDataElCons.p_An)));
        result.setQ_Rp(queryResults.get(mapper.apply(qContServiceDataElCons.q_Rp)));
        result.setQ_Rn(queryResults.get(mapper.apply(qContServiceDataElCons.q_Rn)));

		return result;
	}


    /**
     *
     * @param contZPointId
     * @param timeDetail
     * @param localDatePeriod
     * @param mapper
     * @return
     */
	private ContServiceDataElCons processElConsAggr(Long contZPointId, TimeDetailKey timeDetail,
                                                    LocalDatePeriod localDatePeriod,
                                                    Function<NumberExpression<Double>, NumberExpression<Double>> mapper) {
        checkNotNull(timeDetail);
        checkNotNull(localDatePeriod);
        checkArgument(localDatePeriod.isValidEq());

        Expression<?>[] expr = dataElConsColumnPathList.stream().map(i -> mapper.apply(i)).collect(Collectors.toList()).toArray(new Expression<?>[0]);
        Tuple result = queryDSLService.queryFactory().select(expr)
            .from(qContServiceDataElCons)
            .where(
                qContServiceDataElCons.timeDetailType.eq(timeDetail.getKeyname())
                    .and(qContServiceDataElCons.contZPointId.eq(contZPointId))
                    .and(qContServiceDataElCons.dataDate.between(localDatePeriod.getDateFrom(), localDatePeriod.getDateTo())))
            .fetchOne();


        return consColumnHelperReader(result, mapper);
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

		return processElConsAggr(contZPointId, timeDetail, localDatePeriod, r -> r.avg());

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

        return processElConsAggr(contZPointId, timeDetail, localDatePeriod, r -> r.min());
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

        return processElConsAggr(contZPointId, timeDetail, localDatePeriod, r -> r.max());

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

        return processElConsAggr(contZPointId, timeDetail, localDatePeriod, r -> r.sum());

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


        Expression<?>[] expr = dataElTechColumnPathList.stream().map(i -> i.avg()).collect(Collectors.toList()).toArray(new Expression<?>[0]);
        Tuple resultTuple = queryDSLService.queryFactory().select(expr)
            .from(qContServiceDataElTech)
            .where(
                qContServiceDataElTech.timeDetailType.eq(timeDetail.getKeyname())
                    .and(qContServiceDataElTech.contZPointId.eq(contZPointId))
                    .and(qContServiceDataElTech.dataDate.between(localDatePeriod.getDateFrom(), localDatePeriod.getDateTo())))
            .fetchOne();

        ContServiceDataElTech result = new ContServiceDataElTech();
        result.setU1(resultTuple.get(qContServiceDataElTech.u1.avg()));
        result.setU2(resultTuple.get(qContServiceDataElTech.u2.avg()));
        result.setU3(resultTuple.get(qContServiceDataElTech.u3.avg()));

        result.setI1(resultTuple.get(qContServiceDataElTech.i1.avg()));
        result.setI2(resultTuple.get(qContServiceDataElTech.i2.avg()));
        result.setI3(resultTuple.get(qContServiceDataElTech.i3.avg()));

        result.setK1(resultTuple.get(qContServiceDataElTech.k1.avg()));
        result.setK2(resultTuple.get(qContServiceDataElTech.k2.avg()));
        result.setK3(resultTuple.get(qContServiceDataElTech.k3.avg()));

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

        Expression<?>[] expr = dataElProfileColumnPathList.stream().map(i -> i.avg()).collect(Collectors.toList()).toArray(new Expression<?>[0]);
        Tuple resultTuple = queryDSLService.queryFactory().select(expr)
            .from(qContServiceDataElProfile)
            .where(
                qContServiceDataElProfile.timeDetailType.eq(timeDetail.getKeyname())
                    .and(qContServiceDataElProfile.contZPointId.eq(contZPointId))
                    .and(qContServiceDataElProfile.dataDate.between(localDatePeriod.getDateFrom(), localDatePeriod.getDateTo())))
            .fetchOne();

        ContServiceDataElProfile result = new ContServiceDataElProfile();
        result.setP_Ap(resultTuple.get(qContServiceDataElProfile.p_Ap.avg()));
        result.setP_An(resultTuple.get(qContServiceDataElProfile.p_An.avg()));
        result.setQ_Rp(resultTuple.get(qContServiceDataElProfile.q_Rp.avg()));
        result.setQ_Rn(resultTuple.get(qContServiceDataElProfile.q_Rn.avg()));
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
