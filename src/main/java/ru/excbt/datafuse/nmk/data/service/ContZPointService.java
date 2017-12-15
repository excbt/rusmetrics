package ru.excbt.datafuse.nmk.data.service;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointFullVM;
import ru.excbt.datafuse.nmk.data.model.dto.ObjectTagDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;
import ru.excbt.datafuse.nmk.data.model.support.*;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.repository.*;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContServiceTypeRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointMapper;
import ru.excbt.datafuse.nmk.service.mapper.DeviceObjectMapper;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import javax.persistence.PersistenceException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Сервис для работы с точками учета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.03.2015
 *
 */
@Service
public class ContZPointService implements SecuredRoles {

	private static final Logger log = LoggerFactory.getLogger(ContZPointService.class);
    public static final TemporalAmount LAST_DATA_DATE_DEPTH_DURATION = Duration.ofDays(30);


	private final static boolean CONT_ZPOINT_EX_OPTIMIZE = false;
	private final static String[] CONT_SERVICE_HWATER = new String[] { ContServiceTypeKey.HEAT.getKeyname(),
			ContServiceTypeKey.HW.getKeyname(), ContServiceTypeKey.CW.getKeyname() };

	private final static String[] CONT_SERVICE_EL = new String[] { ContServiceTypeKey.EL.getKeyname() };

	private final static List<String> CONT_SERVICE_HWATER_LIST = Arrays.asList(CONT_SERVICE_HWATER);
	private final static List<String> CONT_SERVICE_EL_LIST = Arrays.asList(CONT_SERVICE_EL);

	private final ContZPointRepository contZPointRepository;

	private final ContObjectService contObjectService;

	private final ContServiceTypeRepository contServiceTypeRepository;

	private final OrganizationService organizationService;

	private final ContZPointSettingModeService contZPointSettingModeService;

	private final TemperatureChartService temperatureChartService;

	private final ObjectAccessService objectAccessService;

	private final ContServiceDataHWaterRepository contServiceDataHWaterRepository;

	private final ContServiceDataElConsRepository contServiceDataElConsRepository;

    private final SubscriberAccessService subscriberAccessService;

    private final ContZPointMapper contZPointMapper;

    private final DeviceObjectMapper deviceObjectMapper;

    private final V_LastDataDateAggrRepository v_lastDataDateAggrRepository;

    private final ContZPointDeviceHistoryService contZPointDeviceHistoryService;

    private final ObjectTagService objectTagService;

    private final DeviceObjectRepository deviceObjectRepository;

    @Autowired
    public ContZPointService(ContZPointRepository contZPointRepository,
                             ContObjectService contObjectService,
                             ContServiceTypeRepository contServiceTypeRepository,
                             OrganizationService organizationService,
                             ContZPointSettingModeService contZPointSettingModeService,
                             TemperatureChartService temperatureChartService,
                             ObjectAccessService objectAccessService,
                             ContServiceDataHWaterRepository contServiceDataHWaterRepository,
                             ContServiceDataElConsRepository contServiceDataElConsRepository,
                             SubscriberAccessService subscriberAccessService,
                             ContZPointMapper contZPointMapper,
                             DeviceObjectMapper deviceObjectMapper,
                             V_LastDataDateAggrRepository v_lastDataDateAggrRepository,
                             ContZPointDeviceHistoryService contZPointDeviceHistoryService, ObjectTagService objectTagService, DeviceObjectRepository deviceObjectRepository) {
        this.contZPointRepository = contZPointRepository;
        this.contObjectService = contObjectService;
        this.contServiceTypeRepository = contServiceTypeRepository;
        this.organizationService = organizationService;
        this.contZPointSettingModeService = contZPointSettingModeService;
        this.temperatureChartService = temperatureChartService;
        this.objectAccessService = objectAccessService;
        this.contServiceDataHWaterRepository = contServiceDataHWaterRepository;
        this.contServiceDataElConsRepository = contServiceDataElConsRepository;
        this.subscriberAccessService = subscriberAccessService;
        this.contZPointMapper = contZPointMapper;
        this.deviceObjectMapper = deviceObjectMapper;
        this.v_lastDataDateAggrRepository = v_lastDataDateAggrRepository;
        this.contZPointDeviceHistoryService = contZPointDeviceHistoryService;
        this.objectTagService = objectTagService;
        this.deviceObjectRepository = deviceObjectRepository;
    }


	/**
	 * /**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Transactional(readOnly = true)
	public ContZPoint findOne(long contZpointId) {
		ContZPoint result = contZPointRepository.findOne(contZpointId);
		if (result != null && result.getDeviceObject() != null) {
            result.getDeviceObject().getId();
//			result.getDeviceObjects().forEach(j -> {
//				j.loadLazyProps();
//			});
		}
		return result;
	}

	@Transactional(readOnly = true)
	public ContZPointFullVM findFullVM(long contZpointId) {
		ContZPoint contZPoint = contZPointRepository.findOne(contZpointId);
		return contZPointMapper.toFullVM(contZPoint);
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContZPoint> findContObjectZPoints(Long contObjectId, PortalUserIds portalUserIds) {
		List<ContZPoint> result = objectAccessService.findAllContZPoints(contObjectId, portalUserIds);

//            contZPointRepository.findByContObjectId(contObjectId);

		result.forEach(i -> {
		    if (i.getDeviceObject() != null) {
                i.getDeviceObject().getId();
            }
//			i.getDeviceObjects().forEach(j -> {
//				j.loadLazyProps();
//			});

			if (i.getTemperatureChart() != null) {
				i.getTemperatureChart().getId();
				i.getTemperatureChart().getChartComment();
			}

		});

		return result;
	}

	@Transactional(readOnly = true)
	public List<ContZPointFullVM> selectContObjectZPointsStatsVM(Long contObjectId, PortalUserIds portalUserIds) {
		List<ContZPoint> zPoints = objectAccessService.findAllContZPoints(contObjectId, portalUserIds)
            .stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).collect(Collectors.toList());

		List<ContZPointFullVM> result = new ArrayList<>();

		List<ContZPointFullVM> resultHWater = makeContZPointStatsVM_Hwater(zPoints);
		List<ContZPointFullVM> resultEl = makeContZPointStatsVM_El(zPoints);

		result.addAll(resultHWater);
		result.addAll(resultEl);

		return result;
	}

    /**
     *
     * @param contZPointId
     * @return
     */
	private LocalDateTime getLastDataDateAggr(final Long contZPointId) {
        V_LastDataDateAggr lastDataDateAggr = v_lastDataDateAggrRepository.findOne(contZPointId);
        return lastDataDateAggr != null ? lastDataDateAggr.getLastDataDate() : null;
    }


    /**
     *
     * @param contZPointList
     * @return
     */
	private List<ContZPointFullVM> makeContZPointStatsVM_Hwater(List<ContZPoint> contZPointList) {
		List<ContZPointFullVM> result = new ArrayList<>();
		MinCheck<Date> minCheck = new MinCheck<>();

		for (ContZPoint zp : contZPointList) {

			if (!CONT_SERVICE_HWATER_LIST.contains(zp.getContServiceTypeKeyname())) {
				continue;
			}

			LocalDateTime zPointLastDate = lastDataDateHelper(LocalDateUtils.asLocalDateTime(minCheck.getValue()),
                date -> contServiceDataHWaterRepository.selectLastDataDateByZPointMax(zp.getId(), LocalDateUtils.asDate(date)));

//                contServiceDataHWaterService.lastDataDateHelper(zp.getId(),
//                LocalDateUtils.asLocalDateTime(minCheck.getValue()));

			if (zPointLastDate == null) {
			    zPointLastDate = getLastDataDateAggr(zp.getId());
            }

			LocalDateTime startDay = zPointLastDate == null ? null : zPointLastDate.truncatedTo(ChronoUnit.DAYS);

			minCheck.check(LocalDateUtils.asDate(startDay));
			//result.add(new ContZPointVO(zp, zPointLastDate));
            ContZPointFullVM fullVM = contZPointMapper.toFullVM(zp);
            fullVM.getTimeDetailLastDates().add(new TimeDetailLastDate(TimeDetailLastDate.ALL, zPointLastDate));
			result.add(fullVM);

		}
		return result;
	}

    /**
     *
     * @param zpointList
     * @return
     */
	private List<ContZPointFullVM> makeContZPointStatsVM_El(List<ContZPoint> zpointList) {
		List<ContZPointFullVM> result = new ArrayList<>();
		MinCheck<Date> minCheck = new MinCheck<>();

		for (ContZPoint zp : zpointList) {

			if (!CONT_SERVICE_EL_LIST.contains(zp.getContServiceTypeKeyname())) {
				continue;
			}

            LocalDateTime zPointLastDate = lastDataDateHelper(LocalDateUtils.asLocalDateTime(minCheck.getValue()),
                date -> contServiceDataElConsRepository.selectLastDataDateByZPointMax(zp.getId(), LocalDateUtils.asDate(date)));

            if (zPointLastDate == null) {
                zPointLastDate = getLastDataDateAggr(zp.getId());
            }

            LocalDateTime startDay = zPointLastDate == null ? null : zPointLastDate.truncatedTo(ChronoUnit.DAYS);

			minCheck.check(LocalDateUtils.asDate(startDay));

            ContZPointFullVM fullVM = contZPointMapper.toFullVM(zp);
            fullVM.getTimeDetailLastDates().add(new TimeDetailLastDate(TimeDetailLastDate.ALL, zPointLastDate));
            result.add(fullVM);
			//result.add(new ContZPointVO(zp, zPointLastDate));
            //result.add(contZPointMapper.toFullVM(zp));

		}
		return result;
	}


    /**
     *
     * @param fromDateTime
     * @param lastDataFunction
     * @return
     */
    private static LocalDateTime lastDataDateHelper(LocalDateTime fromDateTime,
                                                   Function<LocalDateTime, List<Timestamp>> lastDataFunction) {

        LocalDateTime actialFromDate = fromDateTime;

        if (actialFromDate == null) {
            actialFromDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minus(LAST_DATA_DATE_DEPTH_DURATION);
        } else {
            log.debug("MinCheck: {}", actialFromDate);
        }

        List<Timestamp> resultList = lastDataFunction.apply(actialFromDate);

        return resultList.get(0) != null ? resultList.get(0).toLocalDateTime() : null;
    }


	/**
	 *
	 * @param contZpointId
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public boolean checkContZPointOwnership(long contZpointId, long contObjectId) {
		List<?> checkIds = contZPointRepository.findByIdAndContObject(contZpointId, contObjectId);
		return checkIds.size() > 0;
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Long> selectContZPointIds(long contObjectId) {
		return contZPointRepository.findContZPointIds(contObjectId);
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Pair<String, Long>> selectContZPointServiceTypeIds(long contObjectId) {

		List<Object[]> qryResult = contZPointRepository.selectContZPointServiceTypeIds(contObjectId);

		List<Pair<String, Long>> resultPairList = qryResult.stream()
            // contServiceTypeKeyname, contZPointId
				.map(i -> new ImmutablePair<>(DBRowUtil.asString(i[0]), DBRowUtil.asLong(i[1])))
				.collect(Collectors.toList());

		return resultPairList;
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContZPointStatInfo> selectContZPointStatInfo(Long contObjectId) {
		List<ContZPointStatInfo> resultList = new ArrayList<>();
		List<Long> contZPointIds = contZPointRepository.findContZPointIds(contObjectId);

		// Date fromDateTime = null;

		MinCheck<Date> minCheck = new MinCheck<>();

		for (Long id : contZPointIds) {

            LocalDateTime zPointLastDate = lastDataDateHelper(LocalDateUtils.asLocalDateTime(minCheck.getValue()),
                date -> contServiceDataHWaterRepository.selectLastDataDateByZPointMax(id, LocalDateUtils.asDate(date)));

//
//			LocalDateTime zPointLastDate = contServiceDataHWaterService.selectLastDataDate(id,
//                minCheck.getValue().map(i -> LocalDateUtils.asLocalDateTime(i)).orElse(null));

			LocalDateTime startDay = zPointLastDate == null ? null : zPointLastDate.truncatedTo(ChronoUnit.DAYS);

			minCheck.check(LocalDateUtils.asDate(startDay));

			ContZPointStatInfo item = new ContZPointStatInfo(id, LocalDateUtils.asDate(zPointLastDate));
			resultList.add(item);
		}
		return resultList;
	}

	/**
	 *
	 * @param contZPoint
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_ZPOINT_ADMIN })
	public ContZPoint updateContZPoint(ContZPoint contZPoint) {
		Objects.requireNonNull(contZPoint);
		Objects.requireNonNull(contZPoint.getId());

		ContZPoint result = contZPointRepository.save(contZPoint);
		contZPointDeviceHistoryService.saveHistory(result);
        if (result.getDeviceObject() != null) {
            result.getDeviceObject().getId();
        }

//		result.getDeviceObjects().forEach(j -> {
//			j.loadLazyProps();
//		});
		return result;
	}

	/**
	 *
	 * @param contObjectId
	 * @param contServiceTypeKey
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_ZPOINT_ADMIN })
	public ContZPoint createManualZPoint(Long contObjectId, ContServiceTypeKey contServiceTypeKey, LocalDate startDate,
			Integer tsNumber, Boolean isDoublePipe, DeviceObject deviceObject) {
        Objects.requireNonNull(contObjectId);
        Objects.requireNonNull(contServiceTypeKey);
        Objects.requireNonNull(startDate);

		ContServiceType contServiceType = contServiceTypeRepository.findOne(contServiceTypeKey.getKeyname());
        Objects.requireNonNull(contServiceType);

		ContZPoint result = new ContZPoint();

		result.setContObjectId(contObjectId);
		linkToContObject(result);

		result.setContServiceType(contServiceType);
		result.setExSystemKeyname(ExSystemKey.MANUAL.getKeyname());
		result.setIsManualLoading(true);
		result.setStartDate(startDate.toDate());
		result.setTsNumber(tsNumber);
		result.setDoublePipe(isDoublePipe);
		if (deviceObject != null) {
			result.setDeviceObject(deviceObject);
			//getDeviceObjects().add(deviceObject);
		}
        ContZPoint savedContZPoint = contZPointRepository.save(result);
        contZPointDeviceHistoryService.saveHistory(savedContZPoint);
        //subscriberAccessService.grantContZPointAccess();
		return savedContZPoint;
	}


    @Transactional
    @Secured({ ROLE_ZPOINT_ADMIN })
    public ContZPoint createZPoint(ContZPointDTO contZPointDTO) {
        Objects.requireNonNull(contZPointDTO);
        Objects.requireNonNull(contZPointDTO.getContObjectId());
        Objects.requireNonNull(contZPointDTO.getContServiceTypeKeyname());
        Objects.requireNonNull(contZPointDTO.getStartDate());
        Objects.requireNonNull(contZPointDTO.getDeviceObjectId());


        if (contZPointDTO.getId() != null) {
            throw new IllegalArgumentException("ContZPointDTO must new");
        }

        ContServiceType contServiceType = contServiceTypeRepository.findOne(contZPointDTO.getContServiceTypeKeyname());
        Objects.requireNonNull(contServiceType);

        DeviceObject deviceObject = deviceObjectRepository.findOne(contZPointDTO.getDeviceObjectId());
        if (deviceObject == null) {
            throw DBExceptionUtil.newEntityNotFoundException(DeviceObject.class, contZPointDTO.getDeviceObjectId());
        }

        ContZPoint contZPoint = contZPointMapper.toEntity(contZPointDTO);
        contZPoint.setDeviceObject(deviceObject);

        ContZPoint savedContZPoint = contZPointRepository.saveAndFlush(contZPoint);
        contZPointDeviceHistoryService.saveHistory(savedContZPoint);
        return savedContZPoint;
    }


	/**
	 *
	 * @param contZpointId
	 */
	@Transactional
	@Secured({ ROLE_ZPOINT_ADMIN })
	public void deleteManualZPoint(Long contZpointId) {
		ContZPoint contZPoint = findOne(contZpointId);
        Objects.requireNonNull(contZPoint);
		if (ExSystemKey.MANUAL.isNotEquals(contZPoint.getExSystemKeyname())) {
			throw new PersistenceException(String.format("Delete ContZPoint(id=%d) with exSystem=%s is not supported ",
					contZpointId, contZPoint.getExSystemKeyname()));
		}
		contZPointDeviceHistoryService.clearHistory(contZPoint);
		contZPointRepository.delete(contZPoint);
	}

    /**
     *
     * @param contZPointDTO
     * @param userIds
     * @return
     */
    @Transactional
    public ContZPointFullVM createZPointDTO2FullVM(ContZPointDTO contZPointDTO, PortalUserIds userIds) {
        Objects.requireNonNull(contZPointDTO);
        Objects.requireNonNull(contZPointDTO.getContObjectId());
        Objects.requireNonNull(contZPointDTO.getStartDate());
        Objects.requireNonNull(contZPointDTO.getContServiceTypeKeyname());
//        checkNotNull(contZPointDTO.get_activeDeviceObjectId());
        Objects.requireNonNull(contZPointDTO.getRsoId());

        ContZPoint contZPoint = contZPointMapper.toEntity(contZPointDTO);

        //contZPointDTO.setContObjectId(contObjectId);
        //linkToContObject(contZPointDTO);
        //initDeviceObject(contZPointDTO);

        if (contZPointDTO.getDeviceObjectId() != null) {
            contZPoint.setDeviceObject(new DeviceObject().id(contZPointDTO.getDeviceObjectId()));
        }

//        contZPoint.getDeviceObjects().clear();
//        contZPoint.getDeviceObjects().add(new DeviceObject().id(contZPointDTO.get_activeDeviceObjectId()));

//        initContServiceType(contZPointDTO);
//        initRso(contZPointDTO);
        contZPoint.setIsManual(true);

        ContZPoint savedContZPoint = contZPointRepository.save(contZPoint);
        contZPointSettingModeService.initContZPointSettingMode(savedContZPoint.getId());

        subscriberAccessService.grantContZPointAccess(new Subscriber().id(userIds.getSubscriberId()), savedContZPoint);

        contZPointDeviceHistoryService.saveHistory(savedContZPoint);

        ContZPointFullVM contZPointFullVM = contZPointMapper.toFullVM(savedContZPoint);

        contZPointFullVM = saveContZPointTags(contZPointFullVM, contZPointDTO.getTagNames(), userIds);

        return contZPointFullVM;
    }

	/**
	 *
	 * @param contZpointId
	 */
	@Transactional
	@Secured({ ROLE_ZPOINT_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	public void deleteOne(PortalUserIds userIds, Long contZpointId) {
		ContZPoint contZPoint = findOne(contZpointId);
        Objects.requireNonNull(contZPoint);
        contZPointDeviceHistoryService.finishHistory(contZPoint);
		contZPointRepository.save(EntityActions.softDelete(contZPoint));
        subscriberAccessService.revokeContZPointAccess(new Subscriber().id(userIds.getSubscriberId()), contZPoint);

	}

	/**
	 *
	 * @param contZpointId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	public void deleteOnePermanent(Long contZpointId) {
        Objects.requireNonNull(contZpointId);
		ContZPoint contZPoint = findOne(contZpointId);
        Objects.requireNonNull(contZPoint);
		contZPointDeviceHistoryService.clearHistory(contZPoint);
		contZPointSettingModeService.deleteByContZPoint(contZpointId);
		contZPointRepository.delete(contZPoint);
	}

	@Transactional
	@Secured({ ROLE_ZPOINT_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	public ContZPointFullVM updateVM(ContZPointFullVM contZPointFullVM) {
        Objects.requireNonNull(contZPointFullVM);
		ContZPoint contZPoint = contZPointMapper.toEntity(contZPointFullVM);
		contZPoint.setIsManual(true);

		ContZPoint savedContZPoint = contZPointRepository.saveAndFlush(contZPoint);
		contZPointSettingModeService.initContZPointSettingMode(savedContZPoint.getId());
        contZPointDeviceHistoryService.saveHistory(savedContZPoint);

		return contZPointMapper.toFullVM(savedContZPoint);
	}

    /**
     *
     * @param contZPointFullVM
     * @return
     */
	@Transactional
    @Secured({ ROLE_ZPOINT_ADMIN })
	public ContZPointFullVM updateDTO_safe(ContZPointFullVM contZPointFullVM) {

        ContZPoint currentContZPoint = contZPointRepository.findOne(contZPointFullVM.getId());

        currentContZPoint.setCustomServiceName(contZPointFullVM.getCustomServiceName());

        currentContZPoint.setIsManualLoading(contZPointFullVM.getManualLoading());

        ContZPoint savedContZPoint = contZPointRepository.save(currentContZPoint);

		return contZPointMapper.toFullVM(savedContZPoint);
	}

	/**
	 *
	 * @param contZPoint
	 */
	@Deprecated
	private void linkToContObject(ContZPoint contZPoint) {
		ContObject contObject = contObjectService.findContObjectChecked(contZPoint.getContObjectId());
		contZPoint.setContObject(contObject);
	}

	/**
	 *
	 * @return
	 */
	public List<ContServiceType> selectContServiceTypes() {
		List<ContServiceType> serviceTypes = contServiceTypeRepository.selectAll();
		return serviceTypes;
	}

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Transactional
	public List<DeviceObject> selectDeviceObjects(long contZpointId) {
		return contZPointRepository.selectDeviceObjects(contZpointId);
	}

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Transactional
	public List<Long> selectDeviceObjectIds(long contZpointId) {
		return contZPointRepository.selectDeviceObjectIds(contZpointId);
	}

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Transactional
	public Long selectContObjectId(long contZpointId) {
		List<Long> ids = contZPointRepository.selectContObjectByContZPointId(contZpointId);
		return ids.isEmpty() ? null : ids.get(0);
	}

	/**
	 *
	 * @param deviceObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContZPoint> selectContPointsByDeviceObject(Long deviceObjectId) {
		return contZPointRepository.selectContZPointsByDeviceObjectId(deviceObjectId);
	}


    /**
     *
     * @param contZPointFullVM
     * @param tagNames
     * @param portalUserIds
     * @return
     */
	@Transactional
	public ContZPointFullVM saveContZPointTags (ContZPointFullVM contZPointFullVM, Collection<String> tagNames, PortalUserIds portalUserIds) {
	    Objects.requireNonNull(contZPointFullVM);

        if (tagNames != null) {

            List<ObjectTagDTO> dtos =
                tagNames.stream().distinct().map(s -> {
                ObjectTagDTO dto = new ObjectTagDTO();
                dto.setTagName(s);
                dto.setObjectTagKeyname(ObjectTag.contZPointTagKeyname);
                dto.setObjectId(contZPointFullVM.getId());
                return dto;
            }).collect(Collectors.toList());

            List<ObjectTagDTO> resultTags = objectTagService.saveTags(dtos,portalUserIds);
            contZPointFullVM.setTagNames(resultTags.stream().map(ObjectTagDTO::getTagName).collect(Collectors.toSet()));
        }
        return contZPointFullVM;
    }

}
