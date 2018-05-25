package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.WeatherForecast;
import ru.excbt.datafuse.nmk.data.model.WeatherForecastCalc;
import ru.excbt.datafuse.nmk.data.repository.WeatherForecastCalcRepository;
import ru.excbt.datafuse.nmk.data.repository.WeatherForecastRepository;

@Service
public class WeatherForecastService {

	private static final Logger log = LoggerFactory.getLogger(WeatherForecastService.class);

	private static final String FORECAST_TYPE = "day";
	private static final String CALC_TYPE = "forecast24";

	private static final PageRequest LIMIT1_PAGE_REQUEST = new PageRequest(0, 1);

	@Autowired
	private WeatherForecastRepository weatherForecastRepository;

	@Autowired
	private WeatherForecastCalcRepository weatherForecastCalcRepository;

	/**
	 *
	 * @param weatherPlaceId
	 * @param forecastDate
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<WeatherForecastCalc> selectWeatherForecastCalc(Long weatherPlaceId, LocalDate forecastDate) {
		checkNotNull(weatherPlaceId);
		checkNotNull(forecastDate);
		return weatherForecastCalcRepository.selectForecastCalc(weatherPlaceId, forecastDate.toDate(), FORECAST_TYPE,
				CALC_TYPE);
	}

	/**
	 *
	 * @param weatherPlaceId
	 * @param forecastDate
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<WeatherForecastCalc> selectWeatherForecastCalc(Long weatherPlaceId, Date forecastDate) {
		checkNotNull(weatherPlaceId);
		checkNotNull(forecastDate);
		return weatherForecastCalcRepository.selectForecastCalc(weatherPlaceId, forecastDate, FORECAST_TYPE, CALC_TYPE);
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional( readOnly = true)
	public WeatherForecast selectLastWeatherForecast(Long weatherPlaceId, java.time.LocalDate currentDate) {

		Date d = Date.from(currentDate.atTime(23, 59, 59, 0).atZone(ZoneId.systemDefault()).toInstant());

		List<WeatherForecast> forecasts = weatherForecastRepository.selectLastForecast(weatherPlaceId, d,
				LIMIT1_PAGE_REQUEST);

		return forecasts.isEmpty() ? null : forecasts.get(0);
	}

}
