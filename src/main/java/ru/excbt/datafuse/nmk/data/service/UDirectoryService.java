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
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.SubscriberService;

/**
 * Сервис для работы с универсальным справочником
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.03.2015
 *
 */
@Service
public class UDirectoryService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(UDirectoryService.class);

	@Autowired
	private UDirectoryRepository directoryRepository;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional( readOnly = true)
	public UDirectory findOne(long subscriberId, final long uDirectoryId) {
		if (!checkAvailableDirectory(subscriberId, uDirectoryId)) {
			return null;
		}
		UDirectory result = directoryRepository.findOne(uDirectoryId);
		if (result == null) {
			logger.debug("UDirectory (id={}) not found", uDirectoryId);
			return null;
		}
		return result;
	}

	/**
	 *
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<UDirectory> findAll(long subscriberId) {
		return directoryRepository.selectBySubscriber(subscriberId);
	}

	/**
	 *
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<Long> selectAvailableDirectoryIds(long subscriberId) {
		List<Long> directoryIds = directoryRepository.selectDirectoryIds(subscriberId);
		return Collections.unmodifiableList(directoryIds);
	}

	/**
	 *
	 * @param directoryId
	 * @return
	 */
	@Transactional( readOnly = true)
	public boolean checkAvailableDirectory(long subscriberId, long directoryId) {
		List<Long> res = directoryRepository.selectAvailableId(subscriberId, directoryId);
		return !res.isEmpty();
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public UDirectory save(long subscriberId, final UDirectory entity) {
		checkNotNull(entity);
		Subscriber currentSubscriber = subscriberService.selectSubscriber(subscriberId);
		checkNotNull(currentSubscriber, "Empty current SubscrOrg");

		long subscrOrgId = currentSubscriber.getId();

		UDirectory recordToSave = null;
		if (entity.isNew()) {
			recordToSave = new UDirectory();
		} else {

			final long directoryId = entity.getId();

			if (!checkAvailableDirectory(subscriberId, directoryId)) {
				throw new PersistenceException(
						"SubscrOrgId: " + subscrOrgId + ". Directory with ID: " + directoryId + " is not found");
			}
			recordToSave = directoryRepository.findOne(directoryId);
			if (recordToSave == null) {
				throw new PersistenceException(
						"SubscrOrgId: " + subscrOrgId + ". Directory with ID: " + directoryId + " is not found");
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
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void delete(long subscriberId, final long directoryId) {

		if (!checkAvailableDirectory(subscriberId, directoryId)) {
			throw new PersistenceException(
					"SubscriberId: " + subscriberId + ". Directory with ID: " + directoryId + " is not found");
		}

		directoryRepository.delete(directoryId);

	}

}
