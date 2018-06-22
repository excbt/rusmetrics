package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.ContManagement;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.repository.ContManagementRepository;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

/**
 * Сервис для работы с привязкой Объект учета и управляющая компания
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.03.2015
 *
 */
@Service
public class ContManagementService implements SecuredRoles {

	@Autowired
	private ContManagementRepository contManagementRepository;

//	@Autowired
	//private ContObjectService contObjectService;

	@Autowired
	private OrganizationService organizationService;

	@Transactional(readOnly = true)
	public List<ContManagement> selectByContObject(long contObjectId) {
		return contManagementRepository.selectByContObject(contObjectId);
	}

	/**
	 *
	 * @param contObjectId
	 * @param organizationId
	 * @param beginDate
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public ContManagement createManagement(Long contObjectId, Long organizationId, LocalDate beginDate) {

		checkArgument(contObjectId > 0);
		checkArgument(organizationId > 0);
		checkNotNull(beginDate);

		List<ContManagement> checkExists = contManagementRepository.selectByContObject(contObjectId);

		checkExists.forEach(i -> {
			if (i.getEndDate() == null) {
				i.setEndDate(beginDate.toDate());
				contManagementRepository.save(i);
			}
		});

//		ContObject co = contObjectService.findContObjectChecked(contObjectId);
//		if (co == null) {
//			throw new PersistenceException(String.format("ContObject(id=%d) not found", contObjectId));
//		}

        Organization org = organizationService.findOneOrganization(organizationId)
            .orElseThrow(() -> DBExceptionUtil.newEntityNotFoundException(Organization.class, organizationId));


		ContManagement newRecord = new ContManagement();
		newRecord.setContObject(new ContObject().id(contObjectId));
		newRecord.setOrganization(org);
		newRecord.setBeginDate(beginDate.toDate());

		ContManagement result = contManagementRepository.save(newRecord);

		return result;
	}

	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public ContManagement createManagement(ContObject contObject, Long organizationId, LocalDate beginDate) {

		checkNotNull(contObject);
		checkNotNull(organizationId);
		checkNotNull(beginDate);


		Organization org = organizationService.findOneOrganization(organizationId)
            .orElseThrow(() -> DBExceptionUtil.newEntityNotFoundException(Organization.class, organizationId));

		List<ContManagement> checkExists = contManagementRepository.selectByContObject(contObject.getId());

		checkExists.forEach(i -> {
			if (i.getEndDate() == null) {
				i.setEndDate(beginDate.toDate());
				contManagementRepository.save(i);
			}
		});
		ContManagement newRecord = new ContManagement();
		newRecord.setContObject(contObject);
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
	@Transactional( readOnly = true)
	public List<ContManagement> selectActiveManagement(long contObjectId) {
		return contManagementRepository.selectActiveManagement(contObjectId);
	}

	/**
	 *
	 * @param contObjectId
	 * @param organizationId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ContManagement> selectActiveManagement(final ContObject contObject) {
		checkNotNull(contObject);
		checkNotNull(contObject.getId());
		return contManagementRepository.selectActiveManagement(contObject.getId());
	}

	/**
	 *
	 * @param organizationId
	 * @return
	 */
	@Transactional( readOnly = true)
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
	@Transactional( readOnly = true)
	public List<ContManagement> selectContManagement(long contObjectId, long organizationId) {
		checkArgument(contObjectId > 0);
		checkArgument(organizationId > 0);
		return contManagementRepository.selectContMagement(contObjectId, organizationId);
	}

	/**
	 *
	 * @param contManagements
	 */
	@Transactional
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN })
	public void deletePermanent(List<ContManagement> contManagements) {
		checkNotNull(contManagements);
		contManagementRepository.deleteAll(contManagements);
	}

}
