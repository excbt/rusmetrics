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

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContManagement;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.repository.ContManagementRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class ContManagementService implements SecuredRoles {

	@Autowired
	private ContManagementRepository contManagementRepository;

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private OrganizationService organizationService;

	@Transactional(readOnly = true)
	public List<ContManagement> selectAllManagement(long contObjectId) {
		return contManagementRepository.selectAllManagement(contObjectId);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param organizationId
	 * @param beginDate
	 * @return
	 */
	@Transactional (value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public ContManagement createManagement(long contObjectId,
			long organizationId, final DateTime beginDate) {

		checkArgument(contObjectId > 0);
		checkArgument(organizationId > 0);
		checkNotNull(beginDate);

		List<ContManagement> checkExists = contManagementRepository
				.selectAllManagement(contObjectId);

		for (ContManagement cm : checkExists) {
			if (beginDate.toDate().equals(cm.getBeginDate())) {
				throw new PersistenceException(
						String.format(
								"ContManagement with contObject(id=%d) and "
										+ "organization(id=%d) on beginDate(%s) already exists",
								contObjectId, organizationId, beginDate));
			}
		}

		ContObject co = contObjectService.findOneContObject(contObjectId);
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContManagement> selectActiveManagement(long contObjectId) {
		return contManagementRepository.selectActiveManagement(contObjectId);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param organizationId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContManagement> selectActiveManagement(
			final ContObject contObject) {
		checkNotNull(contObject);
		checkNotNull(contObject.getId());
		return contManagementRepository.selectActiveManagement(contObject
				.getId());
	}

	/**
	 * 
	 * @param organizationId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContManagement> selectByOgranization(long organizationId) {
		checkArgument(organizationId > 0);
		;
		return contManagementRepository.selectByOrganization(organizationId);
	}

	/**
	 * 
	 * @param organizationId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContManagement> selectContManagement(long contObjectId,
			long organizationId) {
		checkArgument(contObjectId > 0);
		checkArgument(organizationId > 0);
		return contManagementRepository.selectContMagement(contObjectId,
				organizationId);
	}

}
