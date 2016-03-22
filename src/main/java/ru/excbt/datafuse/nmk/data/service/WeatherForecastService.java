package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.repository.WeatherForecastCalcRepository;
import ru.excbt.datafuse.nmk.data.repository.WeatherForecastRepository;

@Service
public class WeatherForecastService {

	private static final Logger logger = LoggerFactory.getLogger(WeatherForecastService.class);

	@Autowired
	private WeatherForecastRepository weatherForecastRepository;

	@Autowired
	private WeatherForecastCalcRepository weatherForecastCalcRepository;

}
