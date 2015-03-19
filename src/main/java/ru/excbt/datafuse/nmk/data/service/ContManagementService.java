package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContManagement;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.repository.ContManagementRepository;

@Service
@Transactional
public class ContManagementService implements SecuredServiceRoles {

	@Autowired
	private ContManagementRepository contManagementRepository;

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private OrganizationService organizationService;

	// @Secured({ROLE_ADMIN, SUBSCR_ROLE_ADMIN})
	@Transactional(readOnly = true)
	public List<ContManagement> selectAllManagement(long contObjectId,
			long organizationId) {
		return contManagementRepository.selectAllManagement(contObjectId,
				organizationId);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param organizationId
	 * @param beginDate
	 * @return
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	@Transactional
	public ContManagement createManagement(long contObjectId,
			long organizationId, final DateTime beginDate) {

		checkArgument(contObjectId > 0);
		checkArgument(organizationId > 0);
		checkNotNull(beginDate);

		List<ContManagement> checkExists = selectAllManagement(contObjectId,
				organizationId);

		for (ContManagement cm : checkExists) {
			if (beginDate.toDate().equals(cm.getBeginDate())) {
				throw new PersistenceException(
						String.format(
								"ContManagement with contObject(id=%d) and "
										+ "organization(id=%d) on beginDate(%s) already exists",
								contObjectId, organizationId, beginDate));
			}
		}

		ContObject co = contObjectService.findOne(contObjectId);
		if (co == null) {
			throw new PersistenceException(String.format(
					"ContObject(id=%d) not found", contObjectId));
		}

		Organization org = organizationService.findOne(organizationId);
		if (org == null) {
			throw new PersistenceException(String.format(
					"Organiztion(id=%d) not found", organizationId));
		}

		ContManagement newRecord = new ContManagement();
		newRecord.setContObject(co);
		newRecord.setOrganization(org);
		newRecord.setBeginDate(beginDate.toDate());

		ContManagement result = contManagementRepository.save(newRecord);

		return result;
	}

	/**
	 * 
	 * @param contObjectId
	 * @param organizationId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContManagement> selectActiveManagement (long contObjectId,
			long organizationId) {
		return contManagementRepository.selectActiveManagement(contObjectId, organizationId);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param organizationId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContManagement> selectActiveManagement (final ContObject contObject,
			long organizationId) {
		checkNotNull(contObject);
		checkNotNull(contObject.getId());
		return contManagementRepository.selectActiveManagement(contObject.getId(), organizationId);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param organizationId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContManagement> selectActiveManagement (final ContObject contObject,
			final Organization organization) {
		checkNotNull(contObject);
		checkNotNull(contObject.getId());
		checkNotNull(organization);
		checkNotNull(organization.getId());
		return contManagementRepository.selectActiveManagement(contObject.getId(), organization.getId());
	}
	
	/**
	 * 
	 * @param organizationId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContManagement> selectByOgranization(long organizationId) {
		checkArgument(organizationId > 0);;
		return contManagementRepository.selectByOrganization(organizationId);
	}
	
}
