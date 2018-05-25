package ru.excbt.datafuse.nmk.data.service;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.repository.ContObjectFiasRepository;

/**
 * Сервис для работы с ФИАС
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.01.2016
 *
 */
@Service
public class FiasService {

	private static final Logger logger = LoggerFactory.getLogger(FiasService.class);

	@PersistenceContext(unitName = "nmk-p")
	private EntityManager em;

	private final ContObjectFiasRepository contObjectFiasRepository;

    public FiasService(ContObjectFiasRepository contObjectFiasRepository) {
        this.contObjectFiasRepository = contObjectFiasRepository;
    }

    /**
	 *
	 * @param fiasUUID
	 * @return
	 */
	@Transactional( readOnly = true)
	public UUID getCityUUID(UUID fiasUUID) {
		String qString = "SELECT fias.get_city_uuid(:p_uuid);";

		Query query = em.createNativeQuery(qString).setParameter("p_uuid", fiasUUID.toString());

		Object result = query.getSingleResult();

		if (result == null) {
			return null;
		}

		String strResult = (String) result;
		return UUID.fromString(strResult);
	}

	/**
	 *
	 * @param fiasUUID
	 * @return
	 */
	@Transactional( readOnly = true)
	public String getCityName(UUID fiasUUID) {
		String qString = "SELECT fias.get_city_name(:p_uuid);";

		Query query = em.createNativeQuery(qString).setParameter("p_uuid", fiasUUID.toString());

		Object result = query.getSingleResult();

		if (result == null) {
			return null;
		}

		String strResult = (String) result;
		return strResult;
	}

	/**
	 *
	 * @param fiasUUID
	 * @return
	 */
	@Transactional( readOnly = true)
	public String getShortAddr(UUID fiasUUID) {
		String qString = "SELECT fias.get_short_addr(:p_uuid);";

		Query query = em.createNativeQuery(qString).setParameter("p_uuid", fiasUUID.toString());

		Object result = query.getSingleResult();

		if (result == null) {
			return null;
		}

		String strResult = (String) result;
		return strResult;
	}




}
