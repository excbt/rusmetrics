package ru.excbt.datafuse.nmk.data.service;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMeterPeriodSettingsDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMonitorDTO;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColorV2;
import ru.excbt.datafuse.nmk.data.model.keyname.ContObjectSettingModeType;
import ru.excbt.datafuse.nmk.data.model.v.ContObjectGeoPos;
import ru.excbt.datafuse.nmk.data.repository.*;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContObjectSettingModeTypeRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import javax.persistence.PersistenceException;
import javax.persistence.Tuple;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Сервис по работе с объектом учета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 05.02.2015
 *
 */
@Service
public class ContObjectService extends AbstractService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(ContObjectService.class);

	private static final String GEO_POS_JSON_TEMPLATE = "{\"pos\": \"%s %s\"}";

	private final ContObjectRepository contObjectRepository;

	private final ContObjectSettingModeTypeRepository contObjectSettingModeTypeRepository;

	private final SubscriberService subscriberService;

	private final ContObjectFiasRepository contObjectFiasRepository;

	private final ContObjectGeoPosRepository contObjectGeoPosRepository;

	private final ContObjectDaDataService contObjectDaDataService;

	private final TimezoneDefService timezoneDefService;

	private final ContManagementService contManagementService;

	private final FiasService fiasService;

	private final LocalPlaceService localPlaceService;

	private final ContEventMonitorV2Service contEventMonitorV2Service;

	private final WeatherForecastService weatherForecastService;

	private final MeterPeriodSettingRepository meterPeriodSettingRepository;

	private final ContObjectMapper contObjectMapper;

	private final ContObjectFiasService contObjectFiasService;

	private final SubscriberAccessService subscriberAccessService;

	private final ObjectAccessService objectAccessService;

	private final ContZPointAccessRepository contZPointAccessRepository;

    public ContObjectService(ContObjectRepository contObjectRepository,
                             ContObjectSettingModeTypeRepository contObjectSettingModeTypeRepository,
                             SubscriberService subscriberService,
                             ContObjectFiasRepository contObjectFiasRepository,
                             ContObjectGeoPosRepository contObjectGeoPosRepository,
                             ContObjectDaDataService contObjectDaDataService,
                             TimezoneDefService timezoneDefService,
                             ContManagementService contManagementService,
                             FiasService fiasService,
                             LocalPlaceService localPlaceService,
                             ContEventMonitorV2Service contEventMonitorV2Service,
                             WeatherForecastService weatherForecastService,
                             MeterPeriodSettingRepository meterPeriodSettingRepository,
                             ContObjectMapper contObjectMapper, ContObjectFiasService contObjectFiasService, SubscriberAccessService subscriberAccessService, ObjectAccessService objectAccessService, ContZPointAccessRepository contZPointAccessRepository) {
        this.contObjectRepository = contObjectRepository;
        this.contObjectSettingModeTypeRepository = contObjectSettingModeTypeRepository;
        this.subscriberService = subscriberService;
        this.contObjectFiasRepository = contObjectFiasRepository;
        this.contObjectGeoPosRepository = contObjectGeoPosRepository;
        this.contObjectDaDataService = contObjectDaDataService;
        this.timezoneDefService = timezoneDefService;
        this.contManagementService = contManagementService;
        this.fiasService = fiasService;
        this.localPlaceService = localPlaceService;
        this.contEventMonitorV2Service = contEventMonitorV2Service;
        this.weatherForecastService = weatherForecastService;
        this.meterPeriodSettingRepository = meterPeriodSettingRepository;
        this.contObjectMapper = contObjectMapper;
        this.contObjectFiasService = contObjectFiasService;
        this.subscriberAccessService = subscriberAccessService;
        this.objectAccessService = objectAccessService;
        this.contZPointAccessRepository = contZPointAccessRepository;
    }


    /**
     *
      * @param contObjectId
     * @return
     */
    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContObject findContObjectChecked(Long contObjectId) {
		ContObject result = contObjectRepository.findOne(contObjectId);
		if (result == null) {
			throw new PersistenceException(String.format("ContObject(id=%d) is not found", contObjectId));
		}
		return result;
	}

    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public Optional<ContObject> findContObjectOptional(Long contObjectId) {
        ContObject result = contObjectRepository.findOne(contObjectId);

        return result != null ? Optional.of(result) : Optional.empty();
    }


    /**
     *
     * @param contObjectId
     * @return
     */
    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContObjectDTO findContObjectDTO(Long contObjectId) {
		return contObjectMapper.contObjectToDto(findContObjectChecked(contObjectId));
	}


    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContObjectMonitorDTO findContObjectMonitorDTO(SubscriberParam subscriberParam,  Long contObjectId) {
		ContObject contObject = findContObjectChecked(contObjectId);
        contObjectDaDataService.findOneByContObjectId(contObjectId).ifPresent((i) -> contObject.set_daDataSraw(i.getSraw()));
		List<ContObjectMonitorDTO> monitorDTOList = wrapContObjectsMonitorDTO(subscriberParam, Arrays.asList(contObject));
        return monitorDTOList.get(0);
	}

	/**
	 *
	 * @param str
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> findContObjectsByFullName(String str) {
		return contObjectRepository.findByFullNameLikeIgnoreCase(str);
	}

	/**
	 *
	 * @param contObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_CONT_OBJECT_ADMIN })
    @Deprecated
	private ContObject updateContObject(ContObject contObject, Long cmOrganizationId) {
		checkNotNull(contObject);
		checkArgument(!contObject.isNew());

		ContObject currContObject = contObjectRepository.findOne(contObject.getId());
		if (currContObject == null) {
			throw new PersistenceException(String.format("ContObject (ID=%d) not found", contObject.getId()));
		}

		currContObject.updateFromContObject(contObject);

		// Process ContObjectDaData
		ContObjectDaData contObjectDaData = contObjectDaDataService.getOrInitDaData(currContObject);
		checkNotNull(contObjectDaData);
		if (contObject.get_daDataSraw() != null) {
			contObjectDaData.setSraw(contObject.get_daDataSraw());
			contObjectDaData.setIsValid(true);
			contObject.setIsAddressAuto(true);
		} else {
			if (contObject.getFullAddress() == null
					|| !contObject.getFullAddress().equals(contObjectDaData.getSvalue())) {
				contObjectDaData.setSraw(null);
				contObjectDaData.setDataGeoLat(null);
				contObjectDaData.setDataGeoLon(null);
				contObjectDaData.setDataFiasId(null);
				contObjectDaData.setIsValid(false);
			}

		}
		contObjectDaData = contObjectDaDataService.parseIfValid(contObjectDaData);
		contObjectDaData = contObjectDaDataService.saveContObjectDaData(contObjectDaData);
		contObject.setIsAddressAuto(contObjectDaData != null && Boolean.TRUE.equals(contObjectDaData.getIsValid()));

		// Process ContObjectFias

		Optional<ContObjectFias> contObjectFiasOptional = contObjectFiasRepository.findOneByContObjectId(currContObject.getId());
		//ContObjectFias contObjectFias = contObjectFiasRepository.findOneByContObjectId(currContObject.getId()).orElse(null);

        ContObjectFias contObjectFias;

		if (!contObjectFiasOptional.isPresent()) {
			contObjectFias = contObjectFiasService.createConfObjectFias(currContObject);
		} else {
		    contObjectFias = contObjectFiasOptional.get();
			contObjectFias.setFiasFullAddress(contObject.getFullAddress());
			contObjectFias.setGeoFullAddress(contObject.getFullAddress());
		}

		if (contObjectDaData != null && Boolean.TRUE.equals(contObjectDaData.getIsValid())) {
			contObjectFias.setFiasFullAddress(contObjectDaData.getSvalue());
			contObjectFias.setGeoFullAddress(contObjectDaData.getSvalue());
			contObjectFias.setFiasUUID(contObjectDaData.getDataFiasId());
			contObjectFias.setIsGeoRefresh(contObjectDaData.getSvalue() != null);
			String dataJsonGeo = makeJsonGeoString(contObjectDaData);
			contObjectFias.setGeoJson2(dataJsonGeo);
		}

		if (contObjectDaData == null || !Boolean.TRUE.equals(contObjectDaData.getIsValid())) {
			contObjectFias.setFiasUUID(null);
			contObjectFias.setCityFiasUUID(null);
			contObjectFias.setGeoJson(null);
			contObjectFias.setGeoJson2(null);
			contObjectFias.setIsGeoRefresh(true);
		}

		if (contObjectFias.getFiasUUID() != null) {
			UUID cityUUID = fiasService.getCityUUID(contObjectFias.getFiasUUID());
			if (cityUUID != null) {
				contObjectFias.setCityFiasUUID(cityUUID);
				String cityName = fiasService.getCityName(cityUUID);
				contObjectFias.setShortAddress2(cityName);

				localPlaceService.saveCityToLocalPlace(cityUUID);

			}
			String shortAddr = fiasService.getShortAddr(contObjectFias.getFiasUUID());
			contObjectFias.setShortAddress1(shortAddr);
		} else {
			contObjectFias.setShortAddress1(null);
			contObjectFias.setShortAddress2(null);
		}

		contObjectFiasService.saveContObjectFias(currContObject.getId(), contObjectFias);

		currContObject.setIsValidGeoPos(contObjectFias.getGeoJson() != null || contObjectFias.getGeoJson2() != null);
		currContObject.setIsValidFiasUUID(contObjectFias.getFiasUUID() != null);
		currContObject.setIsAddressAuto(contObjectDaData != null && contObjectDaData.getSraw() != null);

		ContObject resultContObject = contObjectRepository.save(currContObject);

        contObjectFiasService.contObjectFiasSetRefreshFlag(currContObject);

		ContManagement cm = currContObject.get_activeContManagement();
		if (cmOrganizationId != null && (cm == null || !cmOrganizationId.equals(cm.getOrganizationId()))) {
			ContManagement newCm = contManagementService.createManagement(resultContObject, cmOrganizationId,
					LocalDate.now());
			currContObject.getContManagements().clear();
			currContObject.getContManagements().add(newCm);
		}

		return resultContObject;
	}



    @Transactional(value = TxConst.TX_DEFAULT)
    @Secured({ ROLE_CONT_OBJECT_ADMIN })
    public ContObject automationUpdate(ContObject contObject, Long cmOrganizationId) {
        checkNotNull(contObject);
        checkArgument(!contObject.isNew());
        //checkArgument(contObject.getTimezoneDefKeyname() != null);

        // Load existing contObject
        Long contObjectId = contObject.getId();
        ContObject currContObject = contObjectRepository.findOne(contObjectId);
        if (currContObject == null) {
            throw new PersistenceException(String.format("ContObject (ID=%d) not found", contObject.getId()));
        }
        currContObject.updateFromContObject(contObject);


        // Process ContObjectDaData
        ContObjectDaData contObjectDaData = contObjectDaDataService.getOrInitDaData(contObject);
        checkNotNull(contObjectDaData);

        if (contObject.haveDaData()) {
            contObjectDaData.setSraw(contObject.get_daDataSraw());
            contObjectDaData.setIsValid(true);
            contObject.setIsAddressAuto(true);
        } else {
            if (contObject.getFullAddress() == null
                || !contObject.getFullAddress().equals(contObjectDaData.getSvalue())) {
                contObjectDaData.clearInValid();
            }

        }

        contObjectDaData = contObjectDaDataService.parseIfValid(contObjectDaData);


        //contObject.setIsAddressAuto(contObjectDaData != null && Boolean.TRUE.equals(contObjectDaData.getIsValid()));

        // Process ContObjectFias

        Optional<ContObjectFias> contObjectFiasOptional = contObjectFiasRepository.findOneByContObjectId(contObjectId);

        ContObjectFias contObjectFias;

        if (!contObjectFiasOptional.isPresent()) {
            contObjectFias = contObjectFiasService.createConfObjectFias(currContObject);
        } else {
            contObjectFias = contObjectFiasOptional.get();
            contObjectFias.setFiasFullAddress(contObject.getFullAddress());
            contObjectFias.setGeoFullAddress(contObject.getFullAddress());
        }

        if (Boolean.TRUE.equals(contObjectDaData.getIsValid())) {
            contObjectFias.copyFormDaData(contObjectDaData);
            contObject.setFullAddress(contObjectDaData.getSvalue());
        } else {
            contObjectFias.clearCodes();
        }

        // fias & contObject initialization
        contObjectFiasService.initCityUUID(contObjectFias);
        localPlaceService.saveCityToLocalPlace(contObjectFias);

        currContObject.setIsValidGeoPos(contObjectFias.getGeoJson() != null || contObjectFias.getGeoJson2() != null);
        currContObject.setIsValidFiasUUID(contObjectFias.getFiasUUID() != null);
        currContObject.setIsAddressAuto(contObjectDaData.isAddressAuto());
        ////

        ContObject resultContObject = contObjectRepository.save(currContObject);

        contObjectDaDataService.saveContObjectDaData(contObjectDaData);
        contObjectFiasService.saveContObjectFias(currContObject.getId(), contObjectFias);

        //contObjectFiasSetRefreshFlag(currContObject);

        // Cont Management
        ContManagement cm = currContObject.get_activeContManagement();
        if (cmOrganizationId != null && (cm == null || !cmOrganizationId.equals(cm.getOrganizationId()))) {
            ContManagement newCm = contManagementService.createManagement(resultContObject, cmOrganizationId,
                LocalDate.now());
            currContObject.getContManagements().clear();
            currContObject.getContManagements().add(newCm);
        }

        return resultContObject;
    }


	/**
	 *
	 * @param contObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN })
    @Deprecated
	private ContObject createContObject(ContObject contObject, Long subscriberId, LocalDate subscrBeginDate,
			Long cmOrganizationId) {

		checkNotNull(contObject);
		checkArgument(contObject.isNew());
		checkArgument(contObject.getTimezoneDefKeyname() != null);

		Subscriber subscriber = subscriberService.selectSubscriber(subscriberId);
		if (subscriber == null) {
			throw new PersistenceException(String.format("Subscriber(id=%d) is not found", subscriberId));
		}

		contObject.setIsManual(true);

		// Processing ContObjectDaData
		ContObjectDaData contObjectDaData = contObjectDaDataService.getOrInitDaData(contObject);
		checkNotNull(contObjectDaData);
		if (contObject.get_daDataSraw() != null) {
			contObjectDaData.setSraw(contObject.get_daDataSraw());
			contObjectDaData.setIsValid(true);
		} else {
			if (contObject.getFullAddress() == null
					|| !contObject.getFullAddress().equals(contObjectDaData.getSvalue())) {
				contObjectDaData.setSraw(null);
				contObjectDaData.setDataGeoLat(null);
				contObjectDaData.setDataGeoLon(null);
				contObjectDaData.setDataFiasId(null);
				contObjectDaData.setIsValid(false);
			}
		}
		contObjectDaData = contObjectDaDataService.parseIfValid(contObjectDaData);

		// Inserting ContObjectFias
		ContObjectFias contObjectFias = contObjectFiasService.createConfObjectFias(contObject);

		if (contObjectDaData != null) {
			contObjectFias.setFiasFullAddress(contObjectDaData.getSvalue());
			contObjectFias.setGeoFullAddress(contObjectDaData.getSvalue());
			contObjectFias.setFiasUUID(contObjectDaData.getDataFiasId());
			contObjectFias.setIsGeoRefresh(contObjectDaData.getSvalue() != null);
			String dataJsonGeo = makeJsonGeoString(contObjectDaData);
			contObjectFias.setGeoJson2(dataJsonGeo);

			contObject.setFullAddress(contObjectDaData.getSvalue());

		}

		if (contObjectFias.getFiasUUID() != null) {
			UUID cityUUID = fiasService.getCityUUID(contObjectFias.getFiasUUID());
			if (cityUUID != null) {
				contObjectFias.setCityFiasUUID(cityUUID);
				String cityName = fiasService.getCityName(cityUUID);
				contObjectFias.setShortAddress2(cityName);

				localPlaceService.saveCityToLocalPlace(cityUUID);
			}
			String shortAddr = fiasService.getShortAddr(contObjectFias.getFiasUUID());
			contObjectFias.setShortAddress1(shortAddr);
		}

		contObject.setIsValidGeoPos(contObjectFias.getGeoJson() != null || contObjectFias.getGeoJson2() != null);
		contObject.setIsValidFiasUUID(contObjectFias.getFiasUUID() != null);
		contObject.setIsAddressAuto(contObjectDaData != null && Boolean.TRUE.equals(contObjectDaData.getIsValid()));

		ContObject resultContObject = contObjectRepository.save(contObject);
		contObjectDaDataService.saveContObjectDaData(contObjectDaData);

        contObjectFiasService.saveContObjectFias(resultContObject.getId(), contObjectFias);

		subscriberAccessService.grantContObjectAccess(subscriber, resultContObject, LocalDateUtils.asLocalDateTime(subscrBeginDate.toDate()));

		if (cmOrganizationId != null) {
			ContManagement newCm = contManagementService.createManagement(resultContObject, cmOrganizationId,
					LocalDate.now());
			resultContObject.getContManagements().clear();
			resultContObject.getContManagements().add(newCm);
		}

		return resultContObject;
	}

	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN })
	public ContObject automationCreate(ContObject contObject, Long subscriberId, java.time.LocalDate subscrBeginDate,
			Long cmOrganizationId) {

		checkNotNull(contObject);
		checkArgument(contObject.isNew());
		checkArgument(contObject.getTimezoneDefKeyname() != null);

		contObject.setIsManual(true);

		// Processing ContObjectDaData
		ContObjectDaData contObjectDaData = contObjectDaDataService.getOrInitDaData(contObject);
		checkNotNull(contObjectDaData);

		if (contObject.haveDaData()) {
			contObjectDaData.setSraw(contObject.get_daDataSraw());
			contObjectDaData.setIsValid(true);
            contObject.setIsAddressAuto(true);
		} else {
			if (contObject.getFullAddress() == null
					|| !contObject.getFullAddress().equals(contObjectDaData.getSvalue())) {
			    contObjectDaData.clearInValid();
			}
		}
		contObjectDaData = contObjectDaDataService.parseIfValid(contObjectDaData);

		// Inserting ContObjectFias
		ContObjectFias contObjectFias = contObjectFiasService.createConfObjectFias(contObject);

		if (Boolean.TRUE.equals(contObjectDaData.getIsValid())) {
		    contObjectFias.copyFormDaData(contObjectDaData);
			contObject.setFullAddress(contObjectDaData.getSvalue());
		} else {
            contObjectFias.clearCodes();
        }

        // fias & contObject initialization
		contObjectFiasService.initCityUUID(contObjectFias);
		localPlaceService.saveCityToLocalPlace(contObjectFias);

		contObject.setIsValidGeoPos(contObjectFias.getGeoJson() != null || contObjectFias.getGeoJson2() != null);
		contObject.setIsValidFiasUUID(contObjectFias.getFiasUUID() != null);
		contObject.setIsAddressAuto(contObjectDaData.isAddressAuto());
        ////

		ContObject resultContObject = contObjectRepository.save(contObject);

		contObjectDaDataService.saveContObjectDaData(contObjectDaData);

        contObjectFiasService.saveContObjectFias(resultContObject.getId(), contObjectFias);

		// Cont Management
		if (cmOrganizationId != null) {
			ContManagement newCm = contManagementService.createManagement(resultContObject, cmOrganizationId,
					LocalDate.now());
			resultContObject.getContManagements().clear();
			resultContObject.getContManagements().add(newCm);
		}

        subscriberAccessService.grantContObjectAccess(new Subscriber().id(subscriberId), resultContObject, subscrBeginDate.atStartOfDay());

		return resultContObject;
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN })
	public void deleteContObject(Long contObjectId, java.time.LocalDate subscrEndDate) {
		checkNotNull(contObjectId);

		ContObject contObject = findContObjectChecked(contObjectId);

		contObject.setIsManual(true);
		softDelete(contObject);

		subscriberAccessService.revokeContObjectAccess(new ContObject().id(contObjectId));

		List<ContObjectFias> contObjectFiasList = contObjectFiasRepository.findByContObjectId(contObjectId);
		contObjectFiasList.forEach(i -> {
			softDelete(i);
		});
		contObjectFiasRepository.save(contObjectFiasList);

		contObjectRepository.save(contObject);

	}

	/**
	 *
	 * @param contObjects
	 * @param subscrEndDate
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN })
	public void deleteManyContObjects(Long[] contObjects, java.time.LocalDate subscrEndDate) {
		checkNotNull(contObjects);
		for (Long i : contObjects) {
			deleteContObject(i, subscrEndDate);
		}
	}

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObjectSettingModeType> selectContObjectSettingModeType() {
		List<ContObjectSettingModeType> resultList = contObjectSettingModeTypeRepository.findAll();
		return resultList;
	}

	/**
	 *
	 * @param contObjectIds
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_CONT_OBJECT_ADMIN })
	public List<Long> updateContObjectCurrentSettingModeType(Long[] contObjectIds, String currentSettingMode,
			Long subscriberId) {
		checkNotNull(contObjectIds);
		checkArgument(contObjectIds.length > 0);
		checkNotNull(subscriberId);
		checkNotNull(currentSettingMode);

		List<Long> updatedIds = new ArrayList<>();

		List<Long> updateCandidateIds = Arrays.asList(contObjectIds);

		List<ContObject> contObjects = objectAccessService.findContObjects(subscriberId);

		List<ContObject> updateCandidate = contObjects.stream().filter((i) -> updateCandidateIds.contains(i.getId()))
				.collect(Collectors.toList());

		for (ContObject co : updateCandidate) {
			if (!currentSettingMode.equals(co.getCurrentSettingMode())) {
				co.setCurrentSettingMode(currentSettingMode);
				co.setSettingModeMDate(new Date());
			}

			contObjectRepository.save(co);
			updatedIds.add(co.getId());
		}

		return updatedIds;
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
//	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
//	public ContObjectFias findContObjectFias(Long contObjectId) {
//		checkNotNull(contObjectId);
//		List<ContObjectFias> vList = contObjectFiasRepository.findByContObjectId(contObjectId);
//		return vList.isEmpty() ? null : vList.get(0);
//	}


	/**
	 *
	 * @param contObjectDaData
	 * @return
	 */
	private String makeJsonGeoString(ContObjectDaData contObjectDaData) {
		if (contObjectDaData.getDataGeoLat() == null || contObjectDaData.getDataGeoLon() == null) {
			return null;
		}
		return String.format(GEO_POS_JSON_TEMPLATE, contObjectDaData.getDataGeoLon().toString(),
				contObjectDaData.getDataGeoLat().toString());
	}


    /**
     *
     * @param contObjects
     * @return
     */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObjectMonitorDTO> wrapContObjectsMonitorDTO(SubscriberParam subscriberParam, List<ContObject> contObjects) {
        return wrapContObjectsMonitorDTO (subscriberParam, contObjects, true);
	}


    /**
     *
     * @param contObjects
     * @param contEventStats
     * @return
     */
    public List<ContObjectMonitorDTO> wrapContObjectsMonitorDTO(SubscriberParam subscriberParam, List<ContObject> contObjects, final boolean contEventStats) {
        checkNotNull(contObjects);

        List<ContObjectMonitorDTO> contObjectMonitorDTOList= contObjects.stream()
            .filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> contObjectMapper.contObjectToMonitorDto(i))
            .collect(Collectors.toList());

        List<Long> contObjectIds = contObjectMonitorDTOList.stream().map(i -> i.getId()).distinct()
            .collect(Collectors.toList());

        Map<Long, Integer> contObjectStats = selectContObjectZPointCounter(subscriberParam, contObjectIds);

        // Cont Event Block
        List<ContEventMonitorV2> contEventMonitors = contEventStats ?
            contEventMonitorV2Service.selectByContObjectIds(contObjectIds) :
            Collections.emptyList();

        final Map<Long, List<ContEventMonitorV2>> contEventMonitorMapList = new HashMap<>();

        contEventMonitors.forEach(i -> {
            List<ContEventMonitorV2> l = contEventMonitorMapList.get(i.getContObjectId());
            if (l == null) {
                l = new ArrayList<>();
                contEventMonitorMapList.put(i.getContObjectId(), l);
            }
            checkNotNull(l);
            l.add(i);
        });

        //

        contObjectMonitorDTOList.forEach(i -> {

            Integer res = contObjectStats.get(i.getId());

            i.getContObjectStats().setContZpointCount(res != null ? res : 0);
            List<ContEventMonitorV2> m = contEventMonitorMapList.get(i.getId());
            if (m != null && !m.isEmpty()) {
                ContEventLevelColorV2 color = contEventMonitorV2Service.sortWorseColor(m);
                if (color != null) {
                    i.getContObjectStats().setContEventLevelColor(color.getKeyname());
                }

            }
        });

        return contObjectMonitorDTOList;
    }


    /**
     *
     * @param contObject
     * @param contEventStats
     * @return
     */
    public ContObjectMonitorDTO wrapContObjectMonitorDTO(SubscriberParam subscriberParam, ContObject contObject, final boolean contEventStats) {
        List<ContObjectMonitorDTO> list = wrapContObjectsMonitorDTO(subscriberParam, Arrays.asList(contObject));
        return list.isEmpty() ? null : list.get(0);
    }

	/**
	 *
	 * @param contObjectIds
	 * @return
	 */
	private Map<Long, Integer> selectContObjectZPointCounter(SubscriberParam subscriberParam, Collection<Long> contObjectIds) {

		if (contObjectIds.isEmpty()) {
			return new HashMap<>();
		}

        List<Object[]> resultList = contZPointAccessRepository.findContObjectZPointStats(subscriberParam.getSubscriberId(), new ArrayList<>(contObjectIds));

//		StringBuilder sqlString = new StringBuilder();
//		sqlString.append(" SELECT cont_object_id, count(*) ");
//		sqlString.append(" FROM cont_zpoint ");
//		sqlString.append(" WHERE cont_object_id IN ( :contObjectIds ) AND deleted = 0 ");
//		sqlString.append(" GROUP BY cont_object_id");
//
//		logger.debug("SQL: {}", sqlString.toString());
//
//		Query q1 = em.createNativeQuery(sqlString.toString());
//
//		q1.setParameter("contObjectIds", contObjectIds);
//
//		@SuppressWarnings("unchecked")
//		List<Object[]> resultList = q1.getResultList();

		return resultList.stream()
				.collect(Collectors.toMap(i -> DBRowUtil.asLong(i[0]), i -> DBRowUtil.asInteger(i[1])));

	}

	/**
	 *
	 * @param contObjectIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObjectGeoPos> selectContObjectsGeoPos(List<Long> contObjectIds) {
		return contObjectIds == null || contObjectIds.isEmpty() ? new ArrayList<>()
				: contObjectGeoPosRepository.selectByContObjectIds(contObjectIds);
	}

	/**
	 *
	 * @param contObjectIds
	 * @return
	 */
	public Map<Long, ContObjectGeoPos> selectContObjectsGeoPosMap(List<Long> contObjectIds) {
		return selectContObjectsGeoPos(contObjectIds).stream()
				.collect(Collectors.toMap(ContObjectGeoPos::getContObjectId, Function.identity()));

	}

	/**
	 *
	 * @param contObjectId
	 * @param currentDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public WeatherForecast selectWeatherForecast(Long contObjectId, java.time.LocalDate currentDate) {

		List<ContObjectFias> co = contObjectFiasRepository.findByContObjectId(contObjectId);
		UUID cityUUID = null;
		if (!co.isEmpty()) {
			cityUUID = co.get(0).getCityFiasUUID();
		}

		LocalPlace localPlace = localPlaceService.selectLocalPlaceByFias(cityUUID);

		return localPlace != null
				? weatherForecastService.selectLastWeatherForecast(localPlace.getWeatherPlaceId(), currentDate) : null;
	}

	/**
	 *
	 * @param contObjectMeterPeriodSettingsDTO
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void updateMeterPeriodSettings(ContObjectMeterPeriodSettingsDTO contObjectMeterPeriodSettingsDTO) {

		List<ContObject> contObjectList = new ArrayList<>();

		if (contObjectMeterPeriodSettingsDTO.isSingle()) {
			ContObject contObject = contObjectRepository.findOne(contObjectMeterPeriodSettingsDTO.getContObjectId());
			if (contObject == null) {
				DBExceptionUtil.entityNotFoundException(ContObject.class, contObjectMeterPeriodSettingsDTO.getContObjectId());
			}
			contObjectList.add(contObject);
		} else if (contObjectMeterPeriodSettingsDTO.isMulti()) {
			contObjectList.addAll(contObjectRepository.findAll(contObjectMeterPeriodSettingsDTO.getContObjectIds()));
		}

		contObjectList.forEach((contObject) -> {
			if (Boolean.TRUE.equals(contObjectMeterPeriodSettingsDTO.getReplace())) {
				contObject.getMeterPeriodSettings().clear();
			}
			for (Map.Entry<String, Long> entry : contObjectMeterPeriodSettingsDTO.getMeterPeriodSettings().entrySet()) {
				MeterPeriodSetting setting = new MeterPeriodSetting();
				setting.setId(entry.getValue());
				contObject.getMeterPeriodSettings().put(entry.getKey(), setting);
			}
			contObjectRepository.save(contObject);
		});
		contObjectRepository.flush();
	}

    /**
     *
     * @param contObjectIds
     * @return
     */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObjectMeterPeriodSettingsDTO> findMeterPeriodSettings(List<Long> contObjectIds) {

		List<Tuple> values = contObjectRepository.findMeterPeriodSettings(contObjectIds);
		Map<Long, ContObjectMeterPeriodSettingsDTO> resultMap = values.stream().map(i -> (Long) i.get(0)).distinct()
				.map(i -> new ContObjectMeterPeriodSettingsDTO().contObjectId(i))
				.collect(Collectors.toMap(k -> k.getContObjectId(), v -> v));

		values.forEach(i -> {
			Long id = (Long) i.get(0);
			try {
				MeterPeriodSetting m = (MeterPeriodSetting) i.get(2);
				resultMap.get(id).putSetting((String) i.get(1), m.getId());
			} catch (Exception e) {
				logger.error("V0:{}, V1:{}, V2:{}", i.get(0), i.get(1), i.get(2));
				throw e;
			}

		});

		return resultMap.entrySet().stream().map(m -> m.getValue()).collect(Collectors.toList());
	}


	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContObjectMeterPeriodSettingsDTO getContObjectMeterPeriodSettings(Long contObjectId) {
		ContObject result = findContObjectChecked(contObjectId);

		ContObjectMeterPeriodSettingsDTO settings = ContObjectMeterPeriodSettingsDTO.builder()
				.contObjectId(contObjectId).build();

		for (Map.Entry<String, MeterPeriodSetting> entry : result.getMeterPeriodSettings().entrySet()) {
			settings.putSetting(entry.getKey(), entry.getValue().getId());
		}

		return settings;
	}

    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public List<ContObjectDTO> mapToDTO(List<ContObject> contObjects) {
        return contObjects.stream().map((i) -> contObjectMapper.contObjectToDto(i)).collect(Collectors.toList());
    }

    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public ContObjectDTO mapToDTO(ContObject contObjects) {
        return contObjectMapper.contObjectToDto(contObjects);
    }

}
