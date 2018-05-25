package ru.excbt.datafuse.nmk.data.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.WeatherPlace;
import ru.excbt.datafuse.nmk.data.repository.WeatherPlaceRepository;

@Service
public class WeatherPlaceService {

	private static final Logger log = LoggerFactory.getLogger(WeatherPlaceService.class);

	private final WeatherPlaceRepository weatherPlaceRepository;

	@Autowired
    public WeatherPlaceService(WeatherPlaceRepository weatherPlaceRepository) {
        this.weatherPlaceRepository = weatherPlaceRepository;
    }

    /**
	 *
	 * @param fiasUuid
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<WeatherPlace> selectWeatherPlaceByFias(UUID fiasUuid) {
		return weatherPlaceRepository.selectWeatherPlacesByFias(fiasUuid);
	}

}
