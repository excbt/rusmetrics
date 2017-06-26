package ru.excbt.datafuse.nmk.data.service;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;

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

    /**
	 *
	 * @param fiasUUID
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
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


	public void initCityUUID(ContObjectFias contObjectFias) {
        if (contObjectFias.getFiasUUID() != null) {
            UUID cityUUID = getCityUUID(contObjectFias.getFiasUUID());
            if (cityUUID != null) {
                contObjectFias.setCityFiasUUID(cityUUID);
                String cityName = getCityName(cityUUID);
                contObjectFias.setShortAddress2(cityName);

//                localPlaceService.saveCityToLocalPlace(cityUUID);
            }
            String shortAddr = getShortAddr(contObjectFias.getFiasUUID());
            contObjectFias.setShortAddress1(shortAddr);
        }

    }


}
