package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.keyname.ContObjectSettingModeType;
import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;
import ru.excbt.datafuse.nmk.data.repository.ContObjectFiasRepository;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContObjectSettingModeTypeRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class ContObjectService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(ContObjectService.class);

	@Autowired
	private ContObjectRepository contObjectRepository;

	@Autowired
	private ContObjectSettingModeTypeRepository contObjectSettingModeTypeRepository;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private ContObjectFiasRepository contObjectFiasRepository;

	@Autowired
	private TimezoneDefService timezoneDefService;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContObject findOne(Long contObjectId) {
		return contObjectRepository.findOne(contObjectId);
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
	public ContObject updateOne(ContObject contObject) {
		checkNotNull(contObject);
		checkArgument(!contObject.isNew());

		ContObject currentObject = contObjectRepository.findOne(contObject.getId());
		if (currentObject == null) {
			throw new PersistenceException(String.format("ContObject (ID=%d) not found", contObject.getId()));
		}
		currentObject.setVersion(contObject.getVersion());
		currentObject.setName(contObject.getName());
		currentObject.setFullName(contObject.getFullName());
		currentObject.setFullAddress(contObject.getFullAddress());
		currentObject.setNumber(contObject.getNumber());
		currentObject.setDescription(contObject.getDescription());
		currentObject.setCurrentSettingMode(contObject.getCurrentSettingMode());
		currentObject.setComment(contObject.getComment());
		currentObject.setOwner(contObject.getOwner());
		currentObject.setOwnerContacts(contObject.getOwnerContacts());
		currentObject.setCwTemp(contObject.getCwTemp());
		currentObject.setHeatArea(contObject.getHeatArea());

		ContObject resultEntity = contObjectRepository.save(currentObject);

		return resultEntity;
	}

	/**
	 * 
	 * @param contObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_CONT_OBJECT_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
	public ContObject createOne(ContObject contObject, Long subscriberId) {

		checkNotNull(contObject);
		checkArgument(contObject.isNew());
		checkArgument(contObject.getTimezoneDefKeyname() != null);

		Subscriber subscriber = subscriberService.findOne(subscriberId);
		if (subscriber == null) {
			throw new PersistenceException(String.format("Subscriber(id=%d) is not found", subscriberId));
		}

		TimezoneDef timezoneDef = timezoneDefService.findOne(contObject.getTimezoneDefKeyname());
		contObject.setTimezoneDef(timezoneDef);

		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_CONT_OBJECT_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
	public void deleteOne(Long contObjectId) {
		checkNotNull(contObjectId);

		ContObject contObject = contObjectRepository.findOne(contObjectId);
		if (contObject == null) {
			throw new PersistenceException(String.format("ContObject(id=%d) is not found", contObjectId));
		}

		contObject.setDeleted(1);
		contObject.setIsManual(true);

		contObjectRepository.save(contObject);
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

		List<ContObject> contObjects = subscriberService.selectSubscriberContObjects(subscriberId);

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

}
