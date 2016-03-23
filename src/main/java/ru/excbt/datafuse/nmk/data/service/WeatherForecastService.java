package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.WeatherForecastCalc;
import ru.excbt.datafuse.nmk.data.repository.WeatherForecastCalcRepository;
import ru.excbt.datafuse.nmk.data.repository.WeatherForecastRepository;

@Service
public class WeatherForecastService {

	private static final Logger logger = LoggerFactory.getLogger(WeatherForecastService.class);

	private static final String FORECAST_TYPE = "day";
	private static final String CALC_TYPE = "forecast24";

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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<WeatherForecastCalc> selectWeatherForecastCalc(Long weatherPlaceId, Date forecastDate) {
		checkNotNull(weatherPlaceId);
		checkNotNull(forecastDate);
		return weatherForecastCalcRepository.selectForecastCalc(weatherPlaceId, forecastDate, FORECAST_TYPE, CALC_TYPE);
	}

}
