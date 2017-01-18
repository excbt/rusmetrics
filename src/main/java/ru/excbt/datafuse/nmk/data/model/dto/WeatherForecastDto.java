/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.12.2016
 * 
 */
public class WeatherForecastDto {

	private Long weatherPlaceId;

	private String weatherForecastType;

	private Date forecastDateTime;

	private BigDecimal temperatureValue;

	public Long getWeatherPlaceId() {
		return weatherPlaceId;
	}

	public void setWeatherPlaceId(Long weatherPlaceId) {
		this.weatherPlaceId = weatherPlaceId;
	}

	public String getWeatherForecastType() {
		return weatherForecastType;
	}

	public void setWeatherForecastType(String weatherForecastType) {
		this.weatherForecastType = weatherForecastType;
	}

	public Date getForecastDateTime() {
		return forecastDateTime;
	}

	public void setForecastDateTime(Date forecastDateTime) {
		this.forecastDateTime = forecastDateTime;
	}

	public BigDecimal getTemperatureValue() {
		return temperatureValue;
	}

	public void setTemperatureValue(BigDecimal temperatureValue) {
		this.temperatureValue = temperatureValue;
	}

}
