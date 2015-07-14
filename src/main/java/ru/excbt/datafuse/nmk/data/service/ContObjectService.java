package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.keyname.ContObjectSettingModeType;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContObjectSettingModeTypeRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class ContObjectService implements SecuredRoles {

	private static final Logger logger = LoggerFactory
			.getLogger(ContObjectService.class);

	@Autowired
	private ContObjectRepository contObjectRepository;

	@Autowired
	private ContObjectSettingModeTypeRepository contObjectSettingModeTypeRepository;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private SubscrUserRepository subscrUserRepository;

	@Autowired
	private SubscrContEventNotifiicationService contEventNotifiicationService;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public ContObject findOneContObject(long id) {
		return contObjectRepository.findOne(id);
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContObject> findContObjectsByFullName(String str) {
		return contObjectRepository.findByFullNameLikeIgnoreCase(str);
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public ContObject updateOneContObject(ContObject entity) {
		checkNotNull(entity);
		checkArgument(!entity.isNew());

		ContObject currentEntity = contObjectRepository.findOne(entity.getId());
		if (currentEntity == null) {
			throw new PersistenceException(String.format(
					"ContObject (ID=%d) not found", entity.getId()));
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
	@Transactional(readOnly = true)
	public List<ContObjectSettingModeType> selectContObjectSettingModeType() {
		List<ContObjectSettingModeType> resultList = contObjectSettingModeTypeRepository
				.findAll();
		return resultList;
	}

	/**
	 * 
	 * @param contObjectIds
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public List<Long> updateContObjectCurrentSettingModeType(
			Long[] contObjectIds, String currentSettingMode, Long subscriberId) {
		checkNotNull(contObjectIds);
		checkArgument(contObjectIds.length > 0);
		checkNotNull(subscriberId);

		List<Long> updatedIds = new ArrayList<>();

		List<Long> updateCandidateIds = Arrays.asList(contObjectIds);

		List<ContObject> contObjects = contObjectRepository
				.selectSubscrContObjects(subscriberId);

		List<ContObject> updateCandidate = contObjects.stream()
				.filter((i) -> updateCandidateIds.contains(i.getId()))
				.collect(Collectors.toList());

		for (ContObject co : updateCandidate) {
			co.setCurrentSettingMode(currentSettingMode);
			contObjectRepository.save(co);
			updatedIds.add(co.getId());
		}

		return updatedIds;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContObject> selectSubscriberContObjects(Long subscriberId) {
		checkNotNull(subscriberId);
		return contObjectRepository.selectSubscrContObjects(subscriberId);
	}

}
