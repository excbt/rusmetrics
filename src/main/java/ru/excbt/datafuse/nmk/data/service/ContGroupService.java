package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContGroup;
import ru.excbt.datafuse.nmk.data.repository.ContGroupItemRepository;
import ru.excbt.datafuse.nmk.data.repository.ContGroupRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class ContGroupService implements SecuredRoles {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportParamsetService.class);

	@Autowired
	private ContGroupRepository contGroupRepository;

	@Autowired
	private ContGroupItemRepository contGroupItemRepository;

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public ContGroup createOne(ContGroup entity) {
		checkNotNull(entity);
		checkArgument(entity.isNew());

		ContGroup result = contGroupRepository.save(entity);

		return result;
	}

	/**
	 * 
	 * @param entity
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(ContGroup entity) {
		checkNotNull(entity);
		contGroupRepository.delete(entity);
	}

	/**
	 * 
	 * @param ContGroup
	 * @return
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public ContGroup updateOne(ContGroup entity) {
		checkNotNull(entity);
		checkArgument(!entity.isNew());

		ContGroup result = null;
		result = contGroupRepository.save(entity);
		return result;
	}

	/**
	 * 
	 * @param id
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(long id) {
		if (contGroupRepository.exists(id)) {
			contGroupRepository.delete(id);
		} else {
			throw new PersistenceException(String.format(
					"Can't delete ContGroup(id=%d)", id));
		}

	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public ContGroup findOne(long contGroupId) {
		ContGroup result = contGroupRepository.findOne(contGroupId);
		return result;
	}

}
