package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContManagement;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectDaData;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.SubscrContObject;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.keyname.ContObjectSettingModeType;
import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;
import ru.excbt.datafuse.nmk.data.repository.ContObjectFiasRepository;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContObjectSettingModeTypeRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class ContObjectService extends AbstractService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(ContObjectService.class);

	private static final String GEO_POS_JSON_TEMPLATE = "{\"pos\": \"%s %s\"}";

	@Autowired
	private ContObjectRepository contObjectRepository;

	@Autowired
	private ContObjectSettingModeTypeRepository contObjectSettingModeTypeRepository;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private ContObjectFiasRepository contObjectFiasRepository;

	@Autowired
	private ContObjectDaDataService contObjectDaDataService;

	@Autowired
	private TimezoneDefService timezoneDefService;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	@Autowired
	private ContManagementService contManagementService;

	@Autowired
	private FiasService fiasService;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContObject findOne(Long contObjectId) {
		ContObject result = contObjectRepository.findOne(contObjectId);
		if (result == null) {
			throw new PersistenceException(String.format("ContObject(id=%d) is not found", contObjectId));
		}
		return result;
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
	public ContObject updateOne(ContObject contObject, Long cmOrganizationId) {
		checkNotNull(contObject);
		checkArgument(!contObject.isNew());

		ContObject currContObject = contObjectRepository.findOne(contObject.getId());
		if (currContObject == null) {
			throw new PersistenceException(String.format("ContObject (ID=%d) not found", contObject.getId()));
		}
		currContObject.setVersion(contObject.getVersion());
		currContObject.setName(contObject.getName());
		currContObject.setFullName(contObject.getFullName());
		currContObject.setFullAddress(contObject.getFullAddress());
		currContObject.setNumber(contObject.getNumber());
		currContObject.setDescription(contObject.getDescription());
		currContObject.setCurrentSettingMode(contObject.getCurrentSettingMode());
		currContObject.setComment(contObject.getComment());
		currContObject.setOwner(contObject.getOwner());
		currContObject.setOwnerContacts(contObject.getOwnerContacts());
		currContObject.setCwTemp(contObject.getCwTemp());
		currContObject.setHeatArea(contObject.getHeatArea());

		if (contObject.getTimezoneDefKeyname() != null) {
			currContObject.setTimezoneDef(timezoneDefService.findOne(contObject.getTimezoneDefKeyname()));
		} else {
			currContObject.setTimezoneDef(null);
		}

		// Process ContObjectDaData
		ContObjectDaData contObjectDaData = contObjectDaDataService.getContObjectDaData(currContObject);
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
		contObjectDaData = contObjectDaDataService.processContObjectDaData(contObjectDaData);
		contObject.setIsAddressAuto(contObjectDaData != null && Boolean.TRUE.equals(contObjectDaData.getIsValid()));

		// Process ContObjectFias
		ContObjectFias contObjectFias = currContObject.getContObjectFias();

		if (contObjectFias == null) {
			contObjectFias = createConfObjectFias(currContObject);
		} else {
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
			}
			String shortAddr = fiasService.getShortAddr(contObjectFias.getFiasUUID());
			contObjectFias.setShortAddress1(shortAddr);
		} else {
			contObjectFias.setShortAddress1(null);
			contObjectFias.setShortAddress2(null);
		}

		contObjectFiasRepository.save(contObjectFias);

		currContObject.setIsValidGeoPos(contObjectFias.getGeoJson() != null || contObjectFias.getGeoJson2() != null);
		currContObject.setIsValidFiasUUID(contObjectFias.getFiasUUID() != null);
		currContObject.setIsAddressAuto(contObjectDaData != null && contObjectDaData.getSraw() != null);

		ContObject resultContObject = contObjectRepository.save(currContObject);

		contObjectFiasSetRefreshFlag(currContObject);

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
	public ContObject createOne(ContObject contObject, Long subscriberId, LocalDate subscrBeginDate,
			Long cmOrganizationId) {

		checkNotNull(contObject);
		checkArgument(contObject.isNew());
		checkArgument(contObject.getTimezoneDefKeyname() != null);

		Subscriber subscriber = subscriberService.findOne(subscriberId);
		if (subscriber == null) {
			throw new PersistenceException(String.format("Subscriber(id=%d) is not found", subscriberId));
		}

		TimezoneDef timezoneDef = timezoneDefService.findOne(contObject.getTimezoneDefKeyname());
		contObject.setTimezoneDef(timezoneDef);
		contObject.setIsManual(true);

		// Processing ContObjectDaData
		ContObjectDaData contObjectDaData = contObjectDaDataService.getContObjectDaData(contObject);
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
		contObjectDaData = contObjectDaDataService.processContObjectDaData(contObjectDaData);

		// Inserting ContObjectFias
		ContObjectFias contObjectFias = createConfObjectFias(contObject);

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
			}
			String shortAddr = fiasService.getShortAddr(contObjectFias.getFiasUUID());
			contObjectFias.setShortAddress1(shortAddr);
		}

		contObject.setIsValidGeoPos(contObjectFias.getGeoJson() != null || contObjectFias.getGeoJson2() != null);
		contObject.setIsValidFiasUUID(contObjectFias.getFiasUUID() != null);
		contObject.setIsAddressAuto(contObjectDaData != null && Boolean.TRUE.equals(contObjectDaData.getIsValid()));

		ContObject resultContObject = contObjectRepository.save(contObject);
		contObjectFiasRepository.save(contObjectFias);

		subscrContObjectService.createOne(resultContObject, subscriber, subscrBeginDate);

		if (cmOrganizationId != null) {
			ContManagement newCm = contManagementService.createManagement(resultContObject, cmOrganizationId,
					LocalDate.now());
			resultContObject.getContManagements().clear();
			resultContObject.getContManagements().add(newCm);
		}

		return resultContObject;
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN })
	public void deleteOne(Long contObjectId, LocalDate subscrEndDate) {
		checkNotNull(contObjectId);

		ContObject contObject = contObjectRepository.findOne(contObjectId);
		if (contObject == null) {
			throw new PersistenceException(String.format("ContObject(id=%d) is not found", contObjectId));
		}

		contObject.setIsManual(true);
		softDelete(contObject);

		List<SubscrContObject> subscrContObjects = subscrContObjectService.selectByContObjectId(contObjectId);
		subscrContObjectService.deleteOne(subscrContObjects, subscrEndDate);

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
	public void deleteMany(Long[] contObjects, LocalDate subscrEndDate) {
		checkNotNull(contObjects);
		for (Long i : contObjects) {
			deleteOne(i, subscrEndDate);
		}
	}

	/**
	 * 
	 * @param contObjectId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_CONT_OBJECT_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
	public void deleteOnePermanent(Long contObjectId) {
		checkNotNull(contObjectId);

		ContObject contObject = contObjectRepository.findOne(contObjectId);
		if (contObject == null) {
			throw new PersistenceException(String.format("ContObject(id=%d) is not found", contObjectId));
		}

		List<SubscrContObject> subscrContObjects = subscrContObjectService.selectByContObjectId(contObjectId);
		subscrContObjectService.deleteOnePermanent(subscrContObjects);

		List<ContManagement> contManagements = contManagementService.selectByContObject(contObjectId);
		contManagementService.deletePermanent(contManagements);

		List<ContObjectFias> contObjectFiasList = contObjectFiasRepository.findByContObjectId(contObjectId);
		contObjectFiasRepository.delete(contObjectFiasList);

		contObjectRepository.delete(contObject);
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

		List<ContObject> contObjects = subscrContObjectService.selectSubscriberContObjects(subscriberId);

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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContObjectFias findContObjectFias(Long contObjectId) {
		checkNotNull(contObjectId);
		List<ContObjectFias> vList = contObjectFiasRepository.findByContObjectId(contObjectId);
		return vList.isEmpty() ? null : vList.get(0);
	}

	/**
	 * 
	 * @param contObject
	 */
	private ContObjectFias createConfObjectFias(ContObject contObject) {
		ContObjectFias contObjectFias = new ContObjectFias();
		contObjectFias.setContObject(contObject);
		contObjectFias.setFiasFullAddress(contObject.getFullAddress());
		contObjectFias.setGeoFullAddress(contObject.getFullAddress());
		contObjectFias.setIsGeoRefresh(true);
		return contObjectFias;
	}

	/**
	 * 
	 * @param contObject
	 */
	private void contObjectFiasSetRefreshFlag(ContObject contObject) {
		checkArgument(!contObject.isNew());

		List<ContObjectFias> contObjectFiasList = contObjectFiasRepository.findByContObjectId(contObject.getId());
		if (contObjectFiasList.size() == 0) {
			contObjectFiasList.add(createConfObjectFias(contObject));
		} else {
			contObjectFiasList.forEach(i -> {
				i.setIsGeoRefresh(true);
			});
		}
		contObjectFiasRepository.save(contObjectFiasList);

	}

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

}
