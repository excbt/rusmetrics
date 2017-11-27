package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.V_AuditUser;
import ru.excbt.datafuse.nmk.data.repository.V_FullUserInfoRepository;

/**
 * Сервис для работы с аудитом пользователей
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.03.2015
 *
 */
@Service
@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
public class V_AuditUserService {

	private static final Logger logger = LoggerFactory.getLogger(V_AuditUserService.class);

//	@Autowired
//	private V_AuditUserRepository auditUserRepository;
//
//	@PersistenceContext(unitName = "nmk-p")
//	private EntityManager em;


	private final DBSessionService dbSessionService;

	private final V_FullUserInfoRepository v_fullUserInfoRepository;

//	private final All

    public V_AuditUserService(DBSessionService dbSessionService, V_FullUserInfoRepository v_fullUserInfoRepository) {
        this.dbSessionService = dbSessionService;
        this.v_fullUserInfoRepository = v_fullUserInfoRepository;
    }

    /**
	 *
	 * @param userName
	 * @return
	 */
//	public V_AuditUser findByUserName22(String userName) {
//
//		List<V_AuditUser> auditUsers = auditUserRepository.findByUserName(userName);
//
//		if (auditUsers.size() == 1) {
//			return auditUsers.get(0);
//		} else if (auditUsers.size() > 0) {
//			logger.error("There is more than 1 AuditUser in system (user_name={})", userName);
//		}
//		return null;
//	}

	/**
	 *
	 * @param userName
	 * @return
	 */
	public V_AuditUser findByUserName(String userName) {

	    return v_fullUserInfoRepository.findOneIdByUserNameIgnoreCase(userName).map(i -> new V_AuditUser().id(i)).orElse(new V_AuditUser().id(0L));

//		Query query = em.createQuery("from AuditUser s where s.userName = :arg1", V_AuditUser.class);
//		query.setParameter("arg1", userName);
//		List<?> auditUsers = query.getResultList();
//
//		if (auditUsers.size() > 1) {
//			logger.error("There is more than 1 AuditUser in system (user_name={})", userName);
//			throw new PersistenceException(String.format("Audit UserName %s is not UNIQE", userName));
//		} else if (auditUsers.size() == 0) {
//			logger.error("Audit UserName (user_name={}) is not FOUND", userName);
//			throw new PersistenceException(String.format("Audit UserName %s is not FOUND", userName));
//		}
//
//		V_AuditUser result = (V_AuditUser) auditUsers.get(0);
//		return new V_AuditUser(result);
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	public V_AuditUser findOne22(long id) {
		return new V_AuditUser().id(id);

	}

	/**
	 *
	 * @param id
	 * @return
	 */
	public V_AuditUser findOne(long id) {
//		Query query = em.createQuery("from AuditUser s where s.id = :arg1");
//		query.setParameter("arg1", id);
//		V_AuditUser result = (V_AuditUser) query.getSingleResult();
//		em.detach(result);
		return new V_AuditUser().id(id);
	}


}
