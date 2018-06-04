package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMeterPeriodSettingsDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMonitorDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.keyname.ContObjectSettingModeType;
import ru.excbt.datafuse.nmk.data.model.support.EntityActions;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.model.v.ContObjectGeoPos;
import ru.excbt.datafuse.nmk.data.repository.*;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContObjectSettingModeTypeRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.ContEventMonitorV3Service;
import ru.excbt.datafuse.nmk.service.SubscriberAccessService;
import ru.excbt.datafuse.nmk.service.SubscriberService;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;
import ru.excbt.datafuse.nmk.service.vm.ContObjectShortInfoVM;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import javax.persistence.PersistenceException;
import javax.persistence.Tuple;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

//import static com.google.common.base.Preconditions.checkArgument;

/**
 * Сервис по работе с объектом учета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 05.02.2015
 *
 */
@Service
public class ContObjectService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(ContObjectService.class);

	private static final String GEO_POS_JSON_TEMPLATE = "{\"pos\": \"%s %s\"}";

	private final ContObjectRepository contObjectRepository;

	private final ContObjectSettingModeTypeRepository contObjectSettingModeTypeRepository;

	private final SubscriberService subscriberService;

	private final ContObjectFiasRepository contObjectFiasRepository;

	private final ContObjectGeoPosRepository contObjectGeoPosRepository;

	private final ContObjectDaDataService contObjectDaDataService;

	private final ContManagementService contManagementService;

	private final FiasService fiasService;

	private final LocalPlaceService localPlaceService;

	private final ContEventMonitorV3Service contEventMonitorV3Service;

	private final WeatherForecastService weatherForecastService;

	private final ContObjectMapper contObjectMapper;

	private final ContObjectFiasService contObjectFiasService;

	private final SubscriberAccessService subscriberAccessService;

	private final ObjectAccessService objectAccessService;

	private final ContZPointAccessRepository contZPointAccessRepository;

	private final ContObjectAccessRepository contObjectAccessRepository;

    public ContObjectService(ContObjectRepository contObjectRepository,
                             ContObjectSettingModeTypeRepository contObjectSettingModeTypeRepository,
                             SubscriberService subscriberService,
                             ContObjectFiasRepository contObjectFiasRepository,
                             ContObjectGeoPosRepository contObjectGeoPosRepository,
                             ContObjectDaDataService contObjectDaDataService,
                             ContManagementService contManagementService,
                             FiasService fiasService,
                             LocalPlaceService localPlaceService,
                             ContEventMonitorV3Service contEventMonitorV3Service,
                             WeatherForecastService weatherForecastService,
                             ContObjectMapper contObjectMapper,
                             ContObjectFiasService contObjectFiasService,
                             SubscriberAccessService subscriberAccessService,
                             ObjectAccessService objectAccessService,
                             ContZPointAccessRepository contZPointAccessRepository, ContObjectAccessRepository contObjectAccessRepository) {
        this.contObjectRepository = contObjectRepository;
        this.contObjectSettingModeTypeRepository = contObjectSettingModeTypeRepository;
        this.subscriberService = subscriberService;
        this.contObjectFiasRepository = contObjectFiasRepository;
        this.contObjectGeoPosRepository = contObjectGeoPosRepository;
        this.contObjectDaDataService = contObjectDaDataService;
        this.contManagementService = contManagementService;
        this.fiasService = fiasService;
        this.localPlaceService = localPlaceService;
        this.contEventMonitorV3Service = contEventMonitorV3Service;
        this.weatherForecastService = weatherForecastService;
        this.contObjectMapper = contObjectMapper;
        this.contObjectFiasService = contObjectFiasService;
        this.subscriberAccessService = subscriberAccessService;
        this.objectAccessService = objectAccessService;
        this.contZPointAccessRepository = contZPointAccessRepository;
        this.contObjectAccessRepository = contObjectAccessRepository;
    }


    /**
     *
      * @param contObjectId
     * @return
     */
    @Transactional( readOnly = true)
	public ContObject findContObjectChecked(Long contObjectId) {
		ContObject result = contObjectRepository.findOne(contObjectId);
		if (result == null) {
			throw new PersistenceException(String.format("ContObject(id=%d) is not found", contObjectId));
		}
		return result;
	}

    @Transactional( readOnly = true)
    public Optional<ContObject> findContObjectOptional(Long contObjectId) {
        ContObject result = contObjectRepository.findOne(contObjectId);

        return result != null ? Optional.of(result) : Optional.empty();
    }


    /**
     *
     * @param contObjectId
     * @return
     */
    @Transactional( readOnly = true)
	public ContObjectDTO findContObjectDTO(Long contObjectId) {
		return contObjectMapper.toDto(findContObjectChecked(contObjectId));
	}


    @Transactional( readOnly = true)
	public ContObjectMonitorDTO findContObjectMonitorDTO(PortalUserIds portalUserIds,  Long contObjectId) {
		ContObject contObject = findContObjectChecked(contObjectId);
        contObjectDaDataService.findOneByContObjectId(contObjectId).ifPresent((i) -> contObject.set_daDataSraw(i.getSraw()));
		List<ContObjectMonitorDTO> monitorDTOList = wrapContObjectsMonitorDTO(portalUserIds, Arrays.asList(contObject));
        objectAccessService.readContObjectAccess(portalUserIds.getSubscriberId(), monitorDTOList);
        return monitorDTOList.get(0);
	}

	/**
	 *
	 * @param str
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ContObject> findContObjectsByFullName(String str) {
		return contObjectRepository.findByFullNameLikeIgnoreCase(str);
	}

	/**
	 *
	 * @param contObject
	 * @return
	 */
	//@Transactional
	@Secured({ ROLE_CONT_OBJECT_ADMIN })
    @Deprecated
	private ContObject updateContObject(ContObject contObject, Long cmOrganizationId) {
		Objects.requireNonNull(contObject);
        Objects.requireNonNull(contObject.getId());


		ContObject currContObject = contObjectRepository.findOne(contObject.getId());
		if (currContObject == null) {
			throw new PersistenceException(String.format("ContObject (ID=%d) not found", contObject.getId()));
		}

		currContObject.updateFromContObject(contObject);

		// Process ContObjectDaData
		ContObjectDaData contObjectDaData = contObjectDaDataService.getOrInitDaData(currContObject);
        Objects.requireNonNull(contObjectDaData);
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
                org.joda.time.LocalDate.now());
			currContObject.getContManagements().clear();
			currContObject.getContManagements().add(newCm);
		}

		return resultContObject;
	}


    @Transactional
    @Secured({ ROLE_CONT_OBJECT_ADMIN })
    public ContObject automationUpdate(ContObject contObject, Long cmOrganizationId) {
        Objects.requireNonNull(contObject);
        Objects.requireNonNull(contObject.getId());
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
        Objects.requireNonNull(contObjectDaData);

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
                org.joda.time.LocalDate.now());
            currContObject.getContManagements().clear();
            currContObject.getContManagements().add(newCm);
        }

        return resultContObject;
    }

    @Transactional
    @Secured({ ROLE_CONT_OBJECT_ADMIN })
    public ContObject automationUpdate(ContObjectDTO contObjectDTO, Long cmOrganizationId) {
	    Objects.requireNonNull(contObjectDTO);
	    if (contObjectDTO.getId() == null) {
            throw new IllegalArgumentException("ContObjectDTO must be new");
        }

        // Load existing contObject
        Long contObjectId = contObjectDTO.getId();
        ContObject currContObject = contObjectRepository.findOne(contObjectId);
        if (currContObject == null) {
            throw DBExceptionUtil.newEntityNotFoundException(ContObject.class, contObjectId);
        }
        currContObject.updateFromContObjectDTO(contObjectDTO);


        // Process ContObjectDaData
        ContObjectDaData contObjectDaData = contObjectDaDataService.getOrInitDaData(currContObject);
        Objects.requireNonNull(contObjectDaData);

        if (contObjectDTO.get_daDataSraw() != null && !contObjectDTO.get_daDataSraw().isEmpty()) {
            contObjectDaData.setSraw(contObjectDTO.get_daDataSraw());
            contObjectDaData.setIsValid(true);
            contObjectDTO.setIsAddressAuto(true);
        } else {
            if (contObjectDTO.getFullAddress() == null
                || !contObjectDTO.getFullAddress().equals(contObjectDaData.getSvalue())) {
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
            contObjectFias.setFiasFullAddress(contObjectDTO.getFullAddress());
            contObjectFias.setGeoFullAddress(contObjectDTO.getFullAddress());
        }

        if (Boolean.TRUE.equals(contObjectDaData.getIsValid())) {
            contObjectFias.copyFormDaData(contObjectDaData);
            contObjectDTO.setFullAddress(contObjectDaData.getSvalue());
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

        ContObject resultContObject = contObjectRepository.saveAndFlush(currContObject);

        contObjectDaDataService.saveContObjectDaData(contObjectDaData);
        contObjectFiasService.saveContObjectFias(currContObject.getId(), contObjectFias);

        //contObjectFiasSetRefreshFlag(currContObject);

        // Cont Management
        ContManagement cm = currContObject.get_activeContManagement();
        if (cmOrganizationId != null && (cm == null || !cmOrganizationId.equals(cm.getOrganizationId()))) {
            ContManagement newCm = contManagementService.createManagement(resultContObject, cmOrganizationId,
                org.joda.time.LocalDate.now());
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
	//@Transactional
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN })
    @Deprecated
	private ContObject createContObject(ContObject contObject, Long subscriberId, org.joda.time.LocalDate subscrBeginDate,
			Long cmOrganizationId) {

        Objects.requireNonNull(contObject);
        if (contObject.getId() == null) {
            throw new IllegalArgumentException();
        }
        Objects.requireNonNull(contObject.getTimezoneDefKeyname());

		Subscriber subscriber = subscriberService.selectSubscriber(subscriberId);
		if (subscriber == null) {
			throw new PersistenceException(String.format("Subscriber(id=%d) is not found", subscriberId));
		}

		contObject.setIsManual(true);

		// Processing ContObjectDaData
		ContObjectDaData contObjectDaData = contObjectDaDataService.getOrInitDaData(contObject);
        Objects.requireNonNull(contObjectDaData);
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

		subscriberAccessService.grantContObjectAccess(resultContObject, LocalDateUtils.asLocalDateTime(subscrBeginDate.toDate()), subscriber);

		if (cmOrganizationId != null) {
			ContManagement newCm = contManagementService.createManagement(resultContObject, cmOrganizationId,
                org.joda.time.LocalDate.now());
			resultContObject.getContManagements().clear();
			resultContObject.getContManagements().add(newCm);
		}

		return resultContObject;
	}

	@Transactional
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN })
	public ContObject automationCreate(ContObject contObject, Long subscriberId, java.time.LocalDate subscrBeginDate,
			Long cmOrganizationId) {

        Objects.requireNonNull(contObject);
		if (contObject.getId() != null) {
		    throw new IllegalArgumentException();
        }
		Objects.requireNonNull (contObject.getTimezoneDefKeyname());

		contObject.setIsManual(true);

		// Processing ContObjectDaData
		ContObjectDaData contObjectDaData = contObjectDaDataService.getOrInitDaData(contObject);
        Objects.requireNonNull(contObjectDaData);

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
                org.joda.time.LocalDate.now());
			resultContObject.getContManagements().clear();
			resultContObject.getContManagements().add(newCm);
		}

        subscriberAccessService.grantContObjectAccess(
            resultContObject,
            subscrBeginDate.atStartOfDay(),
            new Subscriber().id(subscriberId));

		return resultContObject;
	}

    /**
     *
     * @param contObjectDTO
     * @param subscriberId
     * @param subscrBeginDate
     * @param cmOrganizationId
     * @return
     */
	@Transactional
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN })
	public ContObject automationCreate(ContObjectDTO contObjectDTO, Long subscriberId, java.time.LocalDate subscrBeginDate,
			Long cmOrganizationId) {

	    Objects.requireNonNull(contObjectDTO);
	    if (contObjectDTO.getId() != null) {
	        throw new IllegalArgumentException("ContObject must be not new");
        }
	    if (contObjectDTO.getTimezoneDefKeyname() == null) {
	        throw new IllegalArgumentException("ContObjectDTO TimeZoneDefKeyname is not set ");
        }


        ContObject contObject = contObjectMapper.toEntity(contObjectDTO);

		contObject.setIsManual(true);

		// Processing ContObjectDaData
		ContObjectDaData contObjectDaData = contObjectDaDataService.getOrInitDaData(contObject);
		Objects.requireNonNull(contObjectDaData);

        if (contObjectDTO.get_daDataSraw() != null && !contObjectDTO.get_daDataSraw().isEmpty()) {
			contObjectDaData.setSraw(contObjectDTO.get_daDataSraw());
			contObjectDaData.setIsValid(true);
            contObject.setIsAddressAuto(true);
		} else {
			if (contObjectDTO.getFullAddress() == null
					|| !contObjectDTO.getFullAddress().equals(contObjectDaData.getSvalue())) {
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
                org.joda.time.LocalDate.now());
			resultContObject.getContManagements().clear();
			resultContObject.getContManagements().add(newCm);
		}

        subscriberAccessService.grantContObjectAccess(
            resultContObject,
            subscrBeginDate.atStartOfDay(),
            new Subscriber().id(subscriberId));

		return resultContObject;
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN })
	public void deleteContObject(Long contObjectId, LocalDate subscrEndDate) {
        Objects.requireNonNull(contObjectId);

		ContObject contObject = findContObjectChecked(contObjectId);

		contObject.setIsManual(true);
		EntityActions.softDelete(contObject);

		subscriberAccessService.revokeContObjectAccess(new ContObject().id(contObjectId));

		List<ContObjectFias> contObjectFiasList = contObjectFiasRepository.findByContObjectId(contObjectId);
		contObjectFiasList.forEach(i -> {
            EntityActions.softDelete(i);
		});
		contObjectFiasRepository.save(contObjectFiasList);

		contObjectRepository.save(contObject);

	}

	/**
	 *
	 * @param contObjects
	 * @param subscrEndDate
	 */
	@Transactional
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN })
	public void deleteManyContObjects(Long[] contObjects, java.time.LocalDate subscrEndDate) {
        Objects.requireNonNull(contObjects);
		for (Long i : contObjects) {
			deleteContObject(i, subscrEndDate);
		}
	}

	/**
	 *
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ContObjectSettingModeType> selectContObjectSettingModeType() {
		List<ContObjectSettingModeType> resultList = contObjectSettingModeTypeRepository.findAll();
		return resultList;
	}

	/**
	 *
	 * @param contObjectIds
	 */
	@Transactional
	@Secured({ ROLE_CONT_OBJECT_ADMIN })
	public List<Long> updateContObjectCurrentSettingModeType(Long[] contObjectIds, String currentSettingMode,
			Long subscriberId) {
        Objects.requireNonNull(contObjectIds);
        if (contObjectIds.length <= 0) {
            throw new IllegalArgumentException();
        }
        Objects.requireNonNull(subscriberId);
        Objects.requireNonNull(currentSettingMode);

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
//	@Transactional( readOnly = true)
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
	@Transactional( readOnly = true)
	public List<ContObjectMonitorDTO> wrapContObjectsMonitorDTO(PortalUserIds portalUserIds, List<ContObject> contObjects) {
	    List<ContObjectMonitorDTO> monitorDTOS = wrapContObjectsMonitorDTO (portalUserIds, contObjects, true);
        objectAccessService.readContObjectAccess(portalUserIds.getSubscriberId(), monitorDTOS);
	    return monitorDTOS;
	}


    /**
     *
     * @param contObjects
     * @param contEventStats
     * @return
     */
    public List<ContObjectMonitorDTO> wrapContObjectsMonitorDTO(PortalUserIds portalUserIds, List<ContObject> contObjects, final boolean contEventStats) {
        Objects.requireNonNull(contObjects);

        List<ContObjectMonitorDTO> contObjectMonitorDTOList= contObjects.stream()
            .filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> contObjectMapper.contObjectToMonitorDto(i))
            .collect(Collectors.toList());

        List<Long> contObjectIds = contObjectMonitorDTOList.stream().map(i -> i.getId()).distinct()
            .collect(Collectors.toList());

        Map<Long, Integer> contObjectStats = selectContObjectZPointCounter(portalUserIds, contObjectIds);

        // Cont Event Block
        List<ContEventMonitorX> contEventMonitors = contEventStats ?
            contEventMonitorV3Service.selectByContObjectIds(contObjectIds) :
            Collections.emptyList();

        final Map<Long, List<ContEventMonitorX>> contEventMonitorMapList = new HashMap<>();

        contEventMonitors.forEach(i -> {
            List<ContEventMonitorX> l = contEventMonitorMapList.get(i.getContObjectId());
            if (l == null) {
                l = new ArrayList<>();
                contEventMonitorMapList.put(i.getContObjectId(), l);
            }
            Objects.requireNonNull(l);
            l.add(i);
        });

        //

        contObjectMonitorDTOList.forEach(i -> {

            Integer res = contObjectStats.get(i.getId());

            i.getContObjectStats().setContZpointCount(res != null ? res : 0);
            List<ContEventMonitorX> m = contEventMonitorMapList.get(i.getId());
            if (m != null && !m.isEmpty()) {
                ContEventLevelColorKeyV2 color = contEventMonitorV3Service.sortWorseColor(m);
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
    public ContObjectMonitorDTO wrapContObjectMonitorDTO(PortalUserIds portalUserIds, ContObject contObject, final boolean contEventStats) {
        List<ContObjectMonitorDTO> list = wrapContObjectsMonitorDTO(portalUserIds, Arrays.asList(contObject));
        if (list != null) objectAccessService.readContObjectAccess(portalUserIds.getSubscriberId(), list);
        return list.isEmpty() ? null : list.get(0);
    }

	/**
	 *
	 * @param contObjectIds
	 * @return
	 */
	private Map<Long, Integer> selectContObjectZPointCounter(PortalUserIds portalUserIds, Collection<Long> contObjectIds) {

		if (contObjectIds.isEmpty()) {
			return new HashMap<>();
		}

        List<Object[]> resultList = contZPointAccessRepository.findContObjectZPointStats(portalUserIds.getSubscriberId(), new ArrayList<>(contObjectIds));

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
	@Transactional( readOnly = true)
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
	@Transactional( readOnly = true)
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
	@Transactional
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
	@Transactional( readOnly = true)
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
	@Transactional( readOnly = true)
	public ContObjectMeterPeriodSettingsDTO getContObjectMeterPeriodSettings(Long contObjectId) {
		ContObject result = findContObjectChecked(contObjectId);

		ContObjectMeterPeriodSettingsDTO settings = ContObjectMeterPeriodSettingsDTO.builder()
				.contObjectId(contObjectId).build();

		for (Map.Entry<String, MeterPeriodSetting> entry : result.getMeterPeriodSettings().entrySet()) {
			settings.putSetting(entry.getKey(), entry.getValue().getId());
		}

		return settings;
	}

    @Transactional( readOnly = true)
    public List<ContObjectDTO> mapToDTO(List<ContObject> contObjects) {
        return contObjects.stream().map(contObjectMapper::toDto).collect(Collectors.toList());
    }

    @Transactional( readOnly = true)
    public ContObjectDTO mapToDTO(ContObject contObjects) {
        return contObjectMapper.toDto(contObjects);
    }


    @Transactional(readOnly = true)
    public List<ContObjectShortInfoVM> findShortInfo (PortalUserIds portalUserIds) {
	    return contObjectAccessRepository.findAllContObjects(portalUserIds.getSubscriberId()).stream()
            .map(contObjectMapper::toShortInfoVM).collect(Collectors.toList());
    }

}
