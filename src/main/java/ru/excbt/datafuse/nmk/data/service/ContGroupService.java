package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.SubscrContGroup;
import ru.excbt.datafuse.nmk.data.model.SubscrContGroupItem;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.SubscrContGroupItemRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrContGroupRepository;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.security.AuthoritiesConstants;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.dto.SubscrContGroupDTO;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.service.mapper.SubscrContGroupMapper;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

/**
 * Сервис для работы с группами ContGroup объектов ContObject
 *
 * @author STATIC.Kuzovoy
 * @version 1.0
 * @since 27.05.2015
 *
 */

@Service
public class ContGroupService {

	private static final Logger logger = LoggerFactory.getLogger(ReportParamsetService.class);

	private final SubscrContGroupRepository contGroupRepository;

	private final SubscrContGroupItemRepository contGroupItemRepository;

	private final ContObjectMapper contObjectMapper;

	private final SubscrContGroupMapper subscrContGroupMapper;

    public ContGroupService(SubscrContGroupRepository contGroupRepository, SubscrContGroupItemRepository contGroupItemRepository, ContObjectMapper contObjectMapper, SubscrContGroupMapper subscrContGroupMapper) {
        this.contGroupRepository = contGroupRepository;
        this.contGroupItemRepository = contGroupItemRepository;
        this.contObjectMapper = contObjectMapper;
        this.subscrContGroupMapper = subscrContGroupMapper;
    }

    /**
	 *
	 * @param entity
	 * @return
	 */

	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public SubscrContGroup createOne(SubscrContGroup entity, Long[] contObjectIds) {
		checkNotNull(entity);
		checkArgument(entity.isNew());

		SubscrContGroup result = contGroupRepository.save(entity);
		if (contObjectIds != null) {
			updateObjectsToGroup(result.getId(), contObjectIds);
		}

		return result;
	}

	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public SubscrContGroupDTO createOne(SubscrContGroupDTO dto, Long[] contObjectIds) {
		checkNotNull(dto);
		checkArgument(dto.getId() == null);

        SubscrContGroup subscrContGroup = subscrContGroupMapper.toEntity(dto);

		SubscrContGroup savedEntity = contGroupRepository.save(subscrContGroup);
		if (contObjectIds != null) {
			updateObjectsToGroup(savedEntity.getId(), contObjectIds);
		}

		return subscrContGroupMapper.toDto(savedEntity);
	}
	/**
	 *
	 * @param entity
	 */
	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public void deleteOne(SubscrContGroup entity) {
		checkNotNull(entity);
		contGroupRepository.delete(entity);
	}

    /**
     *
     * @param entity
     * @return
     */
	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public SubscrContGroup updateOne(SubscrContGroup entity) {
		checkNotNull(entity);
		checkArgument(!entity.isNew());

		SubscrContGroup result = null;
		result = contGroupRepository.save(entity);
		return result;
	}

	/**
	 *
	 * @param contGroup
	 * @return
	 */
	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public SubscrContGroup updateOne(SubscrContGroup contGroup, Long[] contObjectIds) {
		SubscrContGroup result = updateOne(contGroup);

		if (contObjectIds != null) {
			updateObjectsToGroup(result.getId(), contObjectIds);
		}
		return result;
	}

	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public SubscrContGroupDTO updateOne(SubscrContGroupDTO contGroupDTO, Long[] contObjectIds) {

        Objects.requireNonNull(contGroupDTO.getId());

        SubscrContGroup subscrContGroup = subscrContGroupMapper.toEntity(contGroupDTO);

		if (contObjectIds != null) {
			updateObjectsToGroup(subscrContGroup.getId(), contObjectIds);
		}
		return subscrContGroupMapper.toDto(subscrContGroup);
	}

    /**
     *
     * @param contGroupId
     * @param objectIds
     */
	@Transactional
	public void updateObjectsToGroup(final Long contGroupId, final Long[] objectIds) {
		checkNotNull(contGroupId);
		checkNotNull(objectIds);

		List<Long> newObjectIdList = Arrays.asList(objectIds);

		List<Long> currentIds = contGroupItemRepository.selectObjectIds(contGroupId);
		for (Long currentId : currentIds) {
			if (!newObjectIdList.contains(currentId)) {
				logger.trace("removing objectId:{}", currentId);
				deleteObjectsFromGroup(contGroupId, currentId);
			}
		}

		for (Long newId : newObjectIdList) {
			if (!currentIds.contains(newId)) {
				addObjectToGroup(contGroupId, newId);
			}
		}
	}

    /**
     *
     * @param contGroupId
     * @param contObjectId
     */
	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public void deleteObjectsFromGroup(final Long contGroupId, final Long contObjectId) {
		checkNotNull(contGroupId);
		checkNotNull(contObjectId);

		List<Long> ids = contGroupItemRepository.selectItemIds(contGroupId, contObjectId);

		if (ids.size() > 1) {
			logger.trace("Can't delete ReportParamsetUnit. Too Many Rows. (contGroupId={}, contObjectId={})",
					contGroupId, contObjectId);
			throw new PersistenceException(
					String.format("Can't delete ReportParamsetUnit. Too Many Rows. (contGroupId=%d, contObjectId=%d)",
							contGroupId, contObjectId));
		}
		if (ids.size() == 0) {
			logger.trace("Can't delete ReportParamsetUnit. No Rows Found. (contGroupId={}, contObjectId={})",
					contGroupId, contObjectId);
			throw new PersistenceException(
					String.format("Can't delete ReportParamsetUnit. No Rows Found. (contGroupId=%d, contObjectId=%d)",
							contGroupId, contObjectId));
		}

		contGroupItemRepository.deleteById(ids.get(0));
	}

    /**
     *
     * @param contGroupId
     * @param objectId
     * @return
     */
	@Transactional( readOnly = true)
	public boolean checkContGroupObject(Long contGroupId, Long objectId) {
		checkNotNull(contGroupId);
		checkNotNull(objectId);
		return contGroupItemRepository.selectItemIds(contGroupId, objectId).size() > 0;
	}

    /**
     *
     * @param contGroup
     * @param objectId
     * @return
     */
	@Transactional
	public SubscrContGroupItem addObjectToGroup(SubscrContGroup contGroup, Long objectId) {
		checkNotNull(contGroup);
		checkNotNull(objectId);
		checkArgument(!contGroup.isNew());

		if (checkContGroupObject(contGroup.getId(), objectId)) {
			throw new PersistenceException(String.format(
					"ContGroupItem error. A pair of ContGroup (id=%d) and Object (id=%d) is alredy exists",
					contGroup.getId(), objectId));
		}

		SubscrContGroupItem ci = new SubscrContGroupItem();
		ci.setContObjectId(objectId);
		ci.setContGroup(contGroup);
		SubscrContGroupItem result = contGroupItemRepository.save(ci);

		return result;
	}

    /**
     *
     * @param contGroupId
     * @param objectId
     * @return
     */
	@Transactional
	public SubscrContGroupItem addObjectToGroup(Long contGroupId, Long objectId) {
		checkNotNull(contGroupId);
		SubscrContGroup cg = findOne(contGroupId);
		checkNotNull(cg);
		return addObjectToGroup(cg, objectId);
	}

    /**
     *
     * @param contGroupId
     */
	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public void deleteOne(Long contGroupId) {
		checkNotNull(contGroupId);
		if (contGroupRepository.existsById(contGroupId)) {
			List<Long> ids = contGroupItemRepository.selectItemIds(contGroupId);
			for (Long id : ids) {
				contGroupItemRepository.deleteById(id);
			}
			contGroupRepository.deleteById(contGroupId);
		} else {
			throw new PersistenceException(String.format("Can't delete ContGroup(id=%d)", contGroupId));
		}

	}

	/**
	 *
	 * @param contGroupId
	 * @return
	 */
	@Transactional( readOnly = true)
	public SubscrContGroup findOne(Long contGroupId) {
		checkNotNull(contGroupId);
		SubscrContGroup result = contGroupRepository.findById(contGroupId).orElseThrow(() -> new EntityNotFoundException(SubscrContGroup.class, contGroupId));
		return result;
	}

	/**
	 *
	 * @param contGroupId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ContObjectDTO> selectContGroupObjectsDTO(PortalUserIds portalUserIds, Long contGroupId) {
		checkNotNull(contGroupId);
		checkNotNull(portalUserIds);
		List<ContObject> contObjectList = contGroupItemRepository.selectContGroupObjects(portalUserIds.getSubscriberId(), contGroupId);
		return contObjectMapper.toDto(contObjectList);
	}

	@Transactional( readOnly = true)
	public List<ContObject> selectContGroupObjects(PortalUserIds portalUserIds, Long contGroupId) {
		checkNotNull(contGroupId);
		checkNotNull(portalUserIds);
		List<ContObject> contObjectList = contGroupItemRepository.selectContGroupObjects(portalUserIds.getSubscriberId(), contGroupId);
		return contObjectList;
	}

    /**
     *
     * @param portalUserIds
     * @param contGroupId
     * @return
     */
	@Transactional( readOnly = true)
	public List<ContObjectDTO> selectAvailableContGroupObjects(PortalUserIds portalUserIds, Long contGroupId) {
		checkNotNull(contGroupId);
		checkNotNull(portalUserIds);

        List<ContObject> contObjectList = contGroupItemRepository.selectAvailableContGroupObjects(portalUserIds.getSubscriberId(), contGroupId);

		return contObjectMapper.toDto(contObjectList);
	}

    /**
     *
     * @param portalUserIds
     * @return
     */
	@Transactional( readOnly = true)
	public List<SubscrContGroupDTO> selectSubscriberGroups(PortalUserIds portalUserIds) {
		checkNotNull(portalUserIds);
        List<SubscrContGroup> subscrContGroupList = contGroupRepository.findBySubscriberId(portalUserIds.getSubscriberId());
        return subscrContGroupMapper.toDto(subscrContGroupList);
	}

}
