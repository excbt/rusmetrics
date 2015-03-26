package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.repository.UDirectoryRepository;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class UDirectoryService implements SecuredRoles {

	
	private static final Logger logger = LoggerFactory
			.getLogger(UDirectoryService.class);
	
	@Autowired
	private UDirectoryRepository directoryRepository;

	@Autowired
	private CurrentSubscriberService currentSubscrRoleService;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public UDirectory findOne(final long id) {
		if (!checkAvailableDirectory(id)) {
			return null;
		}
		UDirectory result = directoryRepository.findOne(id);
		if (result == null) {
			logger.debug("UDirectory (id={}) not found", id);
			return null;
		}
		return result;
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<UDirectory> findAll() {
		return directoryRepository.selectBySubscriber(currentSubscrRoleService
				.getSubscrOrgId());
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Long> selectAvailableDirectoryIds() {
		long subscrOrgId = currentSubscrRoleService.getSubscrOrgId();
		List<Long> directoryIds = directoryRepository
				.selectDirectoryIds(subscrOrgId);
		return Collections.unmodifiableList(directoryIds);
	}

	/**
	 * 
	 * @param directoryId
	 * @return
	 */
	@Transactional(readOnly = true)
	public boolean checkAvailableDirectory(long directoryId) {
		long subscrOrgId = currentSubscrRoleService.getSubscrOrgId();
		List<Long> res = directoryRepository.selectAvailableId(subscrOrgId,
				directoryId);
		return !res.isEmpty();
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public UDirectory save(final UDirectory entity) {
		checkNotNull(entity);
		Subscriber currentSubscriber = currentSubscrRoleService.getSubscriber();
		checkNotNull(currentSubscriber, "Empty current SubscrOrg");
		
		long subscrOrgId = currentSubscriber.getId();
		

		UDirectory recordToSave = null;
		if (entity.isNew()) {
			recordToSave = new UDirectory();
		} else {

			final long directoryId = entity.getId();
			
			if (!checkAvailableDirectory(directoryId)) {
				throw new PersistenceException("SubscrOrgId: " + subscrOrgId
						+ ". Directory with ID: " + directoryId + " is not found");
			}			
			recordToSave = directoryRepository.findOne(directoryId);
			if (recordToSave == null) {
				throw new PersistenceException("SubscrOrgId: " + subscrOrgId
						+ ". Directory with ID: " + directoryId + " is not found");
			}
		}

		recordToSave.setDirectoryDescription(entity.getDirectoryDescription());
		recordToSave.setDirectoryName(entity.getDirectoryName());
		recordToSave.setVersion(entity.getVersion());
		recordToSave.setSubscriber(currentSubscriber);
		UDirectory savedRecord = directoryRepository.save(recordToSave);

		return savedRecord;
	}

	
	/**
	 * 
	 * @param directoryId
	 */
	@Transactional
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void delete(final long directoryId) {
		long subscrOrgId = currentSubscrRoleService.getSubscrOrgId();
		
		if (!checkAvailableDirectory(directoryId)) {
			throw new PersistenceException("SubscrOrgId: " + subscrOrgId
					+ ". Directory with ID: " + directoryId + " is not found");
		}		
		
		directoryRepository.delete(directoryId);
		
	}
	
}
