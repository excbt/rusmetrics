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
import ru.excbt.datafuse.nmk.data.model.keyname.ContObjectSettingModeType;
import ru.excbt.datafuse.nmk.data.repository.ContObjectFiasRepository;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
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
	private SubscrUserRepository subscrUserRepository;

	@Autowired
	private ContObjectFiasRepository contObjectFiasRepository;

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
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_CONT_OBJECT_ADMIN })
	public ContObject updateOneContObject(ContObject entity) {
		checkNotNull(entity);
		checkArgument(!entity.isNew());

		ContObject currentEntity = contObjectRepository.findOne(entity.getId());
		if (currentEntity == null) {
			throw new PersistenceException(String.format("ContObject (ID=%d) not found", entity.getId()));
		}
		currentEntity.setVersion(entity.getVersion());
		currentEntity.setName(entity.getName());
		currentEntity.setFullName(entity.getFullName());
		currentEntity.setFullAddress(entity.getFullAddress());
		currentEntity.setNumber(entity.getNumber());
		currentEntity.setDescription(entity.getDescription());
		currentEntity.setCurrentSettingMode(entity.getCurrentSettingMode());
		currentEntity.setComment(entity.getComment());
		currentEntity.setOwner(entity.getOwner());
		currentEntity.setOwnerContacts(entity.getOwnerContacts());
		currentEntity.setCwTemp(entity.getCwTemp());
		currentEntity.setHeatArea(entity.getHeatArea());

		ContObject resultEntity = contObjectRepository.save(currentEntity);

		return resultEntity;
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
