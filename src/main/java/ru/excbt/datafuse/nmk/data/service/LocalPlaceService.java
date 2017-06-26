package ru.excbt.datafuse.nmk.data.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.LocalPlace;
import ru.excbt.datafuse.nmk.data.model.WeatherPlace;
import ru.excbt.datafuse.nmk.data.repository.LocalPlaceRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class LocalPlaceService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(LocalPlaceService.class);

	@Autowired
	private LocalPlaceRepository localPlaceRepository;

	@Autowired
	private FiasService fiasService;

	@Autowired
	private WeatherPlaceService weatherPlaceService;

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<LocalPlace> selectLocalPlaces() {
		return localPlaceRepository.selectLocalPlaces();
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public LocalPlace findLocalPlace(Long id) {
		return localPlaceRepository.findOne(id);
	}

	/**
	 *
	 * @param fiasUUID
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public LocalPlace selectLocalPlaceByFias(UUID fiasUUID) {
		if (fiasUUID == null) {
			return null;
		}
		List<LocalPlace> resultList = localPlaceRepository.selectLocalPlacesByFias(fiasUUID);
		return resultList.isEmpty() ? null : resultList.get(0);
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public LocalPlace saveLocalPlace(LocalPlace entity) {
		return localPlaceRepository.save(entity);
	}

	/**
	 *
	 * @param fiasUuid
	 * @return
	 */
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public LocalPlace saveCityToLocalPlace(UUID fiasUuid) {
	    if (fiasUuid == null) {
	        return null;
        }
		LocalPlace localPlace = selectLocalPlaceByFias(fiasUuid);
		if (localPlace == null) {

			List<WeatherPlace> weatherPlaces = weatherPlaceService.selectWeatherPlaceByFias(fiasUuid);
			if (weatherPlaces.size() != 1) {
				return null;
			}

			localPlace = new LocalPlace();
			localPlace.setFiasUuid(fiasUuid);
			localPlace.setWeatherPlace(weatherPlaces.get(0));
			String cityName = fiasService.getCityName(fiasUuid);
			localPlace.setLocalPlaceName(cityName);
			return saveLocalPlace(localPlace);
		}
		return localPlace;
	}

    @Secured({ ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_ADMIN })
    @Transactional(value = TxConst.TX_DEFAULT)
    public LocalPlace saveCityToLocalPlace(ContObjectFias contObjectFias) {
        return contObjectFias != null ? saveCityToLocalPlace(contObjectFias.getCityFiasUUID()) : null;
    }
}
