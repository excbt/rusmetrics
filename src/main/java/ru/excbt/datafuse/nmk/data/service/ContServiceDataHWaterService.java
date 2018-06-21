package ru.excbt.datafuse.nmk.data.service;

import com.google.common.collect.ImmutableSet;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.QContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterAbs_Csv;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterTotals;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.TimeDetailLastDate;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataHWaterRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.QueryDSLService;
import ru.excbt.datafuse.nmk.service.dto.ContServiceDataHWaterDTO;
import ru.excbt.datafuse.nmk.service.mapper.ContServiceDataHWaterMapper;
import ru.excbt.datafuse.nmk.service.utils.ColumnHelper;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;
import ru.excbt.datafuse.nmk.utils.FileWriterUtils;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;

/**
 * Сервис по работе с данными по горячей воде
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.03.2015
 *
 */
@Service
public class ContServiceDataHWaterService  {

	private static final Logger logger = LoggerFactory.getLogger(ContServiceDataHWaterService.class);

    public static final TemporalAmount LAST_DATA_DATE_DEPTH_DURATION = Duration.ofDays(30);

    public static final TemporalAmount MAX_LAST_DATA_DATE_DEPTH_DURATION = Duration.ofDays(365);

	private static final PageRequest LIMIT1_PAGE_REQUEST = new PageRequest(0, 1);

	private static final Set<String> HWATER_SERVICE_TYPE_SET = ImmutableSet.of(ContServiceTypeKey.CW.getKeyname(),
			ContServiceTypeKey.HW.getKeyname(), ContServiceTypeKey.HEAT.getKeyname());


	private final ContServiceDataHWaterRepository contServiceDataHWaterRepository;

	private final ContZPointService contZPointService;

	private final DeviceObjectService deviceObjectService;

	private final HWatersCsvService hWatersCsvService;

	private final ContServiceDataHWaterMapper dataHWaterMapper;

	private final QueryDSLService queryDSLService;

	@Autowired
    public ContServiceDataHWaterService(ContServiceDataHWaterRepository contServiceDataHWaterRepository, ContZPointService contZPointService, DeviceObjectService deviceObjectService, HWatersCsvService hWatersCsvService, ContServiceDataHWaterMapper dataHWaterMapper, QueryDSLService queryDSLService) {
        this.contServiceDataHWaterRepository = contServiceDataHWaterRepository;
        this.contZPointService = contZPointService;
        this.deviceObjectService = deviceObjectService;
        this.hWatersCsvService = hWatersCsvService;
        this.dataHWaterMapper = dataHWaterMapper;
        this.queryDSLService = queryDSLService;
    }

    /**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Transactional( readOnly = true)
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
	//@Transactional( readOnly = true)
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
	@Transactional( readOnly = true)
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
	@Transactional( readOnly = true)
	public List<ContServiceDataHWater> selectByContZPoint(long contZpointId, TimeDetailKey timeDetail,
                                                          org.joda.time.LocalDateTime beginDate,
                                                          org.joda.time.LocalDateTime endDate) {
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
	@Transactional( readOnly = true)
	public List<ContServiceDataHWaterDTO> selectDTOByContZPoint(long contZpointId, TimeDetailKey timeDetail,
			LocalDatePeriod datePeriod) {
		List<ContServiceDataHWater> dataHWaterList = selectByContZPoint(contZpointId, timeDetail, datePeriod);
		return dataHWaterMapper.toDto(dataHWaterList);
	}

    /**
     *
     * @param contZpointId
     * @param timeDetail
     * @param datePeriod
     * @return
     */
	@Transactional( readOnly = true)
	public List<ContServiceDataHWater> selectByContZPoint(long contZpointId, TimeDetailKey timeDetail,
			LocalDatePeriod datePeriod) {
		checkArgument(contZpointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(datePeriod, "beginDate is null");
		checkArgument(datePeriod.isValid());

		List<ContServiceDataHWater> dataHWaterList = contServiceDataHWaterRepository.selectByZPoint(contZpointId, timeDetail.getKeyname(),
            datePeriod.getDateFrom(), datePeriod.getDateTo());

		return dataHWaterList;
	}

	/**
	 *
	 * @param contZpointId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional( readOnly = true)
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
     * @param pageRequest
     * @return
     */
	@Transactional( readOnly = true)
	public Page<ContServiceDataHWaterDTO> selectByContZPoint(long contZpointId, TimeDetailKey timeDetail,
                                                             LocalDatePeriod datePeriod, PageRequest pageRequest) {
		checkArgument(contZpointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(datePeriod, "beginDate is null");
		checkArgument(datePeriod.isValid());
		checkNotNull(pageRequest);

		return contServiceDataHWaterRepository.selectByZPoint(contZpointId, timeDetail.getKeyname(),
				datePeriod.getDateFrom(), datePeriod.getDateTo(), pageRequest).map(i -> dataHWaterMapper.toDto(i));
	}

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Transactional( readOnly = true)
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
	@Transactional( readOnly = true)
	public LocalDateTime selectLastDataDate(long contZpointId, LocalDateTime fromDateTime) {
		checkArgument(contZpointId > 0);

		LocalDateTime actialFromDate = fromDateTime;

		if (actialFromDate == null) {
			actialFromDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minus(LAST_DATA_DATE_DEPTH_DURATION);
		} else {
			logger.debug("MinCheck: {}", actialFromDate);
		}

		List<Timestamp> resultList = contServiceDataHWaterRepository.selectLastDataDateByZPointMax(contZpointId,
            LocalDateUtils.asDate(actialFromDate));

//		if (resultList.get(0) == null) {
//			resultList = contServiceDataHWaterRepository.selectLastDataDateByZPointMax(contZpointId,
//                LocalDateUtils.asDate(actialFromDate.minus(MAX_LAST_DATA_DATE_DEPTH_DURATION)));
//		}

		return resultList.get(0) != null ? resultList.get(0).toLocalDateTime() : null;
	}

	/**
	 *
	 * @param contZpointId
	 * @param fromDateTime
	 * @return
	 */
	@Transactional( readOnly = true)
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
     * @return
     */
	@Transactional( readOnly = true)
	public LocalDateTime selectLastDataDate(long contZpointId) {
		return selectLastDataDate(contZpointId, null);
	}

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Transactional( readOnly = true)
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
	@Transactional( readOnly = true)
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
	@Transactional( readOnly = true)
	public ContServiceDataHWaterTotals selectContZPoint_Totals(long contZpointId, TimeDetailKey timeDetail,
                                                               org.joda.time.LocalDateTime beginDate, org.joda.time.LocalDateTime endDate) {

		checkNotNull(timeDetail);
		checkNotNull(beginDate);
		checkNotNull(endDate);
		checkArgument(beginDate.compareTo(endDate) <= 0);

		logger.debug("selectContZPointTotals: contZPoint:{}; timeDetailType:{}; beginDate:{}; endDate:{}", contZpointId,
				timeDetail.getKeyname(), beginDate.toDate(), endDate.toDate());


        QContServiceDataHWater qContServiceDataHWater = QContServiceDataHWater.contServiceDataHWater;


        JPAQuery<Tuple> qry = queryDSLService.queryFactory().select(

            qContServiceDataHWater.m_in.sum(),
            qContServiceDataHWater.m_out.sum(),
            qContServiceDataHWater.m_delta.sum(),

            qContServiceDataHWater.h_in.sum(),
            qContServiceDataHWater.h_out.sum(),
            qContServiceDataHWater.h_delta.sum(),

            qContServiceDataHWater.v_in.sum(),
            qContServiceDataHWater.v_out.sum(),
            qContServiceDataHWater.v_delta.sum())

            .from(qContServiceDataHWater)
            .where(qContServiceDataHWater.timeDetailType.eq(timeDetail.getKeyname())
                .and(qContServiceDataHWater.contZPointId.eq(contZpointId))
                .and(qContServiceDataHWater.dataDate.between(beginDate.toDate(), endDate.toDate()))
                .and(qContServiceDataHWater.deleted.eq(0)));


        Tuple tuple = qry.fetchOne();

		ContServiceDataHWaterTotals result = new ContServiceDataHWaterTotals();

		result.setM_in(tuple.get(qContServiceDataHWater.m_in.sum()));
		result.setM_out(tuple.get(qContServiceDataHWater.m_out.sum()));
		result.setM_delta(tuple.get(qContServiceDataHWater.m_delta.sum()));

		result.setH_in(tuple.get(qContServiceDataHWater.h_in.sum()));
		result.setH_out(tuple.get(qContServiceDataHWater.h_out.sum()));
		result.setH_delta(tuple.get(qContServiceDataHWater.h_delta.sum()));

		result.setV_in(tuple.get(qContServiceDataHWater.v_in.sum()));
		result.setV_out(tuple.get(qContServiceDataHWater.v_out.sum()));
		result.setV_delta(tuple.get(qContServiceDataHWater.v_delta.sum()));

		result.setContZPointId(contZpointId);
		result.setBeginDate(beginDate.toDate());
		result.setEndDate(beginDate.toDate());

		result.setTimeDetailType(timeDetail.getKeyname());

		return result;
	}

	@Transactional( readOnly = true)
	public ContServiceDataHWater selectContZPoint_Avgs(long contZpointId, TimeDetailKey timeDetail,
			LocalDatePeriod period) {

		checkNotNull(timeDetail);
		checkNotNull(period);
		checkArgument(period.isValidEq());

        QContServiceDataHWater qContServiceDataHWater = QContServiceDataHWater.contServiceDataHWater;

        JPAQuery<Tuple> qry = queryDSLService.queryFactory().select(

            qContServiceDataHWater.t_in.avg(),
            qContServiceDataHWater.t_out.avg(),
            qContServiceDataHWater.t_cold.avg(),
            qContServiceDataHWater.t_outdoor.avg(),

            qContServiceDataHWater.m_in.avg(),
            qContServiceDataHWater.m_out.avg(),
            qContServiceDataHWater.m_delta.avg(),

            qContServiceDataHWater.v_in.avg(),
            qContServiceDataHWater.v_out.avg(),
            qContServiceDataHWater.v_delta.avg(),

            qContServiceDataHWater.h_in.avg(),
            qContServiceDataHWater.h_out.avg(),
            qContServiceDataHWater.h_delta.avg(),

            qContServiceDataHWater.p_in.avg(),
            qContServiceDataHWater.p_out.avg(),
            qContServiceDataHWater.p_delta.avg()
        )

            .from(qContServiceDataHWater)
            .where(qContServiceDataHWater.timeDetailType.eq(timeDetail.getKeyname())
                .and(qContServiceDataHWater.contZPointId.eq(contZpointId))
                .and(qContServiceDataHWater.dataDate.between(period.getDateFrom(), period.getDateTo()))
                .and(qContServiceDataHWater.deleted.eq(0)));

        Tuple tuple = qry.fetchOne();

		ContServiceDataHWater result = new ContServiceDataHWater();
		result.setT_in(tuple.get(qContServiceDataHWater.t_in.avg()));
		result.setT_out(tuple.get(qContServiceDataHWater.t_out.avg()));
		result.setT_cold(tuple.get(qContServiceDataHWater.t_cold.avg()));
		result.setT_outdoor(tuple.get(qContServiceDataHWater.t_outdoor.avg()));

		result.setM_in(tuple.get(qContServiceDataHWater.m_in.avg()));
		result.setM_out(tuple.get(qContServiceDataHWater.m_out.avg()));
		result.setM_delta(tuple.get(qContServiceDataHWater.m_delta.avg()));

		result.setV_in(tuple.get(qContServiceDataHWater.v_in.avg()));
		result.setV_out(tuple.get(qContServiceDataHWater.v_out.avg()));
		result.setV_delta(tuple.get(qContServiceDataHWater.v_delta.avg()));

		result.setH_in(tuple.get(qContServiceDataHWater.h_in.avg()));
		result.setH_out(tuple.get(qContServiceDataHWater.h_out.avg()));
		result.setH_delta(tuple.get(qContServiceDataHWater.h_delta.avg()));

		result.setP_in(tuple.get(qContServiceDataHWater.p_in.avg()));
		result.setP_out(tuple.get(qContServiceDataHWater.p_out.avg()));
		result.setP_delta(tuple.get(qContServiceDataHWater.p_delta.avg()));

		result.setWorkTime(tuple.get(qContServiceDataHWater.workTime.avg()));
		result.setFailTime(tuple.get(qContServiceDataHWater.failTime.avg()));

		return result;

	}

	/**
	 *
	 * @param contZpointId
	 * @param localDateTime
	 * @return
	 */
	//@Transactional( readOnly = true)
	private ContServiceDataHWater selectLastAbsData(long contZpointId, org.joda.time.LocalDateTime localDateTime) {

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
	@Transactional( readOnly = true)
	public ContServiceDataHWater selectLastAbsData(long contZpointId, TimeDetailKey timeDetail,
                                                   org.joda.time.LocalDateTime localDateTime, boolean isEndDate) {

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
            org.joda.time.LocalDateTime ldt = new org.joda.time.LocalDateTime(dataDateLimit);
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
	@Transactional( readOnly = true)
	public List<ContServiceDataHWaterAbs_Csv> selectDataAbs_Csv(long contZpointId, TimeDetailKey timeDetail,
			DateTime beginDate, DateTime endDate) {

		List<ContServiceDataHWater> srcDataList = selectByContZPoint(contZpointId, timeDetail, beginDate, endDate);

		List<ContServiceDataHWaterAbs_Csv> cvsDataList = new ArrayList<>();
		try {

			for (ContServiceDataHWater data : srcDataList) {
				ContServiceDataHWaterAbs_Csv cvsData;
				cvsData = ContServiceDataHWaterAbs_Csv.newInstance(data);
				ContServiceDataHWater abs = selectLastAbsData(data.getContZPointId(), timeDetail,
						new org.joda.time.LocalDateTime(data.getDataDate()), false);
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
	@Transactional
	@Secured({ SecuredRoles.ROLE_ADMIN, SecuredRoles.ROLE_SUBSCR_ADMIN })
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

		if (zpoint.getDeviceObject() == null) {

			logger.debug("Device Object is not found. Create new");

			deviceObject = deviceObjectService.createManualDeviceObject();
			logger.debug("Cont Object is saved. Id:{}", deviceObject.getId());

			zpoint.setDeviceObject(deviceObject);
			contZPointService.updateContZPoint(zpoint);

			logger.debug("ContZPoint is saved. Id:{}", zpoint.getId());
		} else {
			deviceObject = zpoint.getDeviceObject();
		}

		Optional<ContServiceDataHWater> checkIsNewElements = inData.stream().filter((i) -> !i.isNew()).findAny();

		checkState(!checkIsNewElements.isPresent(), "Elements in data list is not new");

		final DeviceObject dObject = deviceObject;
		inData.forEach((d) -> {
			d.setContZPointId(contZpointId);
			//d.setTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname());
            if (dObject != null) {
                d.setDeviceObjectId(dObject.getId());
            }

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
	@Transactional
	@Secured({ SecuredRoles.ROLE_ADMIN, SecuredRoles.ROLE_SUBSCR_ADMIN })
	public List<ContServiceDataHWater> deleteManualDataHWater(Long contZpointId, LocalDatePeriod localDatePeriod,
			TimeDetailKey timeDetailKey, File outFile) {

		checkNotNull(contZpointId);
		checkNotNull(localDatePeriod);
		checkNotNull(timeDetailKey);

		ContZPoint zpoint = contZPointService.findOne(contZpointId);

		checkNotNull(zpoint, String.format("ContZPoint with id:%d is not found", contZpointId));

		checkState(BooleanUtils.isTrue(zpoint.getIsManualLoading()),
				String.format("Manual Loading and Deleting for ContZPoint with id:%d is not allowed", contZpointId));

		List<ContServiceDataHWater> deleteCandidate = contServiceDataHWaterRepository.selectByZPoint(contZpointId, timeDetailKey.getKeyname(),
            localDatePeriod.getDateFrom(), localDatePeriod.getDateTo());

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
	@Transactional( readOnly = true)
	public List<TimeDetailLastDate> selectTimeDetailLastDate(long contZpointId) {
		checkArgument(contZpointId > 0);

		List<TimeDetailLastDate> resultList = new ArrayList<>();

		List<Object[]> qryResultList = contServiceDataHWaterRepository.selectTimeDetailLastDataByZPoint(contZpointId);

		for (Object[] row : qryResultList) {

			logger.info("Data Type: {}", row[1].getClass());

            String timeDetailKeyname = DBRowUtil.asString(row[0]);
            Timestamp lastDateTimestamp = DBRowUtil.asTimestamp(row[1]);

			if (lastDateTimestamp != null) {
                TimeDetailLastDate item = new TimeDetailLastDate(timeDetailKeyname,lastDateTimestamp.toLocalDateTime());
                resultList.add(item);
            }
		}

		return resultList;

	}

	/**
	 *
	 * @param contZpointIds
	 * @return
	 */
	@Transactional( readOnly = true)
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
	@Transactional( readOnly = true)
	public HashMap<Long, List<TimeDetailLastDate>> selectTimeDetailLastDateMapByPair(
			List<Pair<String, Long>> idServiceTypePairs) {
		checkArgument(idServiceTypePairs != null);

		List<Long> contZPointIds = idServiceTypePairs.stream()
				.filter(i -> HWATER_SERVICE_TYPE_SET.contains(i.getLeft())).map(i -> i.getRight())
				.collect(Collectors.toList());

		if (contZPointIds.isEmpty()) {
		    return new HashMap<>();
        } else
    		return selectTimeDetailLastDateMap(contZPointIds);
}

}
