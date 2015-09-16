package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.repository.AuditUserRepository;

@Service
@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
public class AuditUserService {

	private static final Logger logger = LoggerFactory
			.getLogger(AuditUserService.class);

	@Autowired
	private AuditUserRepository auditUserRepository;

	@PersistenceContext(unitName = "nmk-p")
	private EntityManager em;

	/**
	 * 
	 * @param userName
	 * @return
	 */
	public AuditUser findByUserName22(String userName) {

		List<AuditUser> auditUsers = auditUserRepository
				.findByUserName(userName);

		if (auditUsers.size() == 1) {
			return auditUsers.get(0);
		} else if (auditUsers.size() > 0) {
			logger.error(
					"There is more than 1 AuditUser in system (user_name={})",
					userName);
		}
		return null;
	}

	/**
	 * 
	 * @param userName
	 * @return
	 */
	public AuditUser findByUserName(String userName) {

		Query query = em.createQuery(
				"from AuditUser s where s.userName = :arg1", AuditUser.class);
		query.setParameter("arg1", userName);
		List<?> auditUsers = query.getResultList();

		if (auditUsers.size() > 1) {
			logger.error(
					"There is more than 1 AuditUser in system (user_name={})",
					userName);
			throw new PersistenceException(String.format(
					"Audit UserName %s is not UNIQE", userName));
		} else if (auditUsers.size() == 0) {
			logger.error("Audit UserName (user_name={}) is not FOUND", userName);
			throw new PersistenceException(String.format(
					"Audit UserName %s is not FOUND", userName));
		}

		AuditUser result = (AuditUser) auditUsers.get(0);
		return new AuditUser(result);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public AuditUser findOne22(long id) {
		return auditUserRepository.findOne(id);

	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public AuditUser findOne(long id) {
		Query query = em.createQuery("from AuditUser s where s.id = :arg1");
		query.setParameter("arg1", id);
		AuditUser result = (AuditUser) query.getSingleResult();
		em.detach(result);
		return result;
	}

}
