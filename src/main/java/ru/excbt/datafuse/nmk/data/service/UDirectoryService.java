package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.repository.UDirectoryRepository;

@Service
@Transactional
public class UDirectoryService implements SecuredServiceRoles {

	@Autowired
	private UDirectoryRepository directoryRepository;

	@Autowired
	private CurrentSubscrOrgService currentSubscrOrgService;

	@Transactional(readOnly = true)
	public UDirectory findOne(final long id) {
		if (!checkAvailableDirectory(id)) {
			// long subscrOrgId = currentSubscrOrgService.getSubscrOrgId();
			// throw new PersistenceException("SubscrOrgId: " + subscrOrgId +
			// ". Directory with ID: " + id + " is not found");
			return null;
		}
		UDirectory result = directoryRepository.findOne(id);
		return result;
	}

	@Transactional(readOnly = true)
	public List<UDirectory> findAll() {
		return directoryRepository.selectBySubscrOrg(currentSubscrOrgService
				.getSubscrOrgId());
	}

	@Transactional(readOnly = true)
	public List<Long> selectAvailableDirectoryIds() {
		long subscrOrgId = currentSubscrOrgService.getSubscrOrgId();
		List<Long> directoryIds = directoryRepository
				.selectDirectoryIds(subscrOrgId);
		return Collections.unmodifiableList(directoryIds);
	}

	@Transactional(readOnly = true)
	public boolean checkAvailableDirectory(long directoryId) {
		long subscrOrgId = currentSubscrOrgService.getSubscrOrgId();
		List<Long> res = directoryRepository.selectAvailableId(subscrOrgId,
				directoryId);
		return !res.isEmpty();
	}

	@Transactional
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public UDirectory save(final UDirectory entity) {
		checkNotNull(entity);

		long subscrOrgId = currentSubscrOrgService.getSubscrOrgId();



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

		//UDirectoryNode savedDirectoryNode = directoryNodeService.save(entity.getDirectoryNode());
		//recordToSave.setDirectoryNode(savedDirectoryNode);
		UDirectory savedRecord = directoryRepository.save(recordToSave);

		return savedRecord;
	}

	@Transactional
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void delete(final long directoryId) {
		long subscrOrgId = currentSubscrOrgService.getSubscrOrgId();
		
		if (!checkAvailableDirectory(directoryId)) {
			throw new PersistenceException("SubscrOrgId: " + subscrOrgId
					+ ". Directory with ID: " + directoryId + " is not found");
		}		
		
		directoryRepository.delete(directoryId);
		
	}
	
}
