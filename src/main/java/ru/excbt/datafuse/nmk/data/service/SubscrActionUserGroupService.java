package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.SubscrActionGroup;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUserGroup;
import ru.excbt.datafuse.nmk.data.repository.SubscrActionUserGroupRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class SubscrActionUserGroupService implements SecuredRoles {

	private static final Logger logger = LoggerFactory
			.getLogger(SubscrActionUserGroupService.class);

	@Autowired
	private SubscrActionUserGroupRepository subscrActionUserGroupRepository;

	/**
	 * 
	 * @param reportParamsetUnitId
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteUserGroup(final long subscrActionUserId,
			final long subscrActionGroupId) {

		List<Long> ids = subscrActionUserGroupRepository.selectSubscrActionUserGroupIds(
				subscrActionUserId, subscrActionGroupId);

		if (ids.size() > 1) {
			throw new PersistenceException(
					String.format(
							"Can't delete SubscrActionUserGroup. Too Many Rows. (subscrActionUserId=%d, subscrActionGroupId=%d)",
							subscrActionUserId, subscrActionGroupId));
		}
		if (ids.size() == 0) {
			throw new PersistenceException(
					String.format(
							"Can't delete SubscrActionUserGroup. No data found. (subscrActionUserId=%d, subscrActionGroupId=%d)",
							subscrActionUserId, subscrActionGroupId));
		}

		subscrActionUserGroupRepository.delete(ids.get(0));
	}

	/**
	 * 
	 * @param subscrActionUserId
	 * @param subscrActionGroupId
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void addUserGroup(final long subscrActionUserId,
			final long subscrActionGroupId) {
		SubscrActionUserGroup item = new SubscrActionUserGroup();
		item.setSubscrActionUserId(subscrActionUserId);
		item.setSubscrActionGroupId(subscrActionGroupId);
		subscrActionUserGroupRepository.save(item);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param userId
	 * @param groupIds
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void updateUserToGroups(long userId, Long[] groupIds) {

		checkNotNull(groupIds);

		List<Long> newGroupIdList = Arrays.asList(groupIds);

		List<Long> currentGroupIds = subscrActionUserGroupRepository
				.selectGroupIdsByUser(userId);

		for (Long currentId : currentGroupIds) {
			if (!newGroupIdList.contains(currentId)) {
				logger.trace("deleteUserGroup (userId:{}, groupId:{})", userId,
						currentId);
				deleteUserGroup(userId, currentId);
			}
		}

		for (Long newId : newGroupIdList) {
			if (!currentGroupIds.contains(newId)) {
				logger.trace("addUserGroup (userId:{}, groupId:{})", userId,
						newId);
				addUserGroup(userId, newId);
			}
		}

	}

	/**
	 * 
	 * @param subscriberId
	 * @param userId
	 * @param groupIds
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void updateGroupToUsers(long groupId, Long[] userIds) {

		checkNotNull(userIds);

		List<Long> newUserIdList = Arrays.asList(userIds);

		List<Long> currentUserIds = subscrActionUserGroupRepository
				.selectUserIdsByGroup(groupId);

		for (Long currentId : currentUserIds) {
			if (!newUserIdList.contains(currentId)) {
				logger.trace("deleteUserGroup (userId:{}, groupId:{})",
						currentId, groupId);
				deleteUserGroup(currentId, groupId);
			}
		}

		for (Long newId : newUserIdList) {
			if (!currentUserIds.contains(newId)) {
				logger.trace("addUserGroup (userId:{}, groupId:{})", newId,
						groupId);
				addUserGroup(newId, groupId);
			}
		}

	}

	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteByUser(long subscrActionUserId) {
		List<Long> toDelGroupIdList = subscrActionUserGroupRepository
				.selectGroupIdsByUser(subscrActionUserId);
		for (Long grpId : toDelGroupIdList) {
			deleteUserGroup(subscrActionUserId, grpId);
		}
	}

	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteByGroup(long subscrActionGroupId) {
		List<Long> toDelUserIdList = subscrActionUserGroupRepository
				.selectUserIdsByGroup(subscrActionGroupId);
		for (Long userId : toDelUserIdList) {
			deleteUserGroup(userId, subscrActionGroupId);
		}
	}

	/**
	 * 
	 * @param subscriberId
	 * @param subscrActionGroupId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<SubscrActionUser> selectUsersByGroup(long subscrActionGroupId) {
		return subscrActionUserGroupRepository
				.selectUsersByGroup(subscrActionGroupId);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param subscrActionUserId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<SubscrActionGroup> selectGroupsByUser(long subscrActionUserId) {
		return subscrActionUserGroupRepository
				.selectGroupsByUser(subscrActionUserId);
	}

}
