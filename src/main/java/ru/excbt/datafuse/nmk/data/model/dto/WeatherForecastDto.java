/**
 *
 */
package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.12.2016
 *
 */
@Getter
@Setter
public class WeatherForecastDto {

	private Long weatherPlaceId;

	private String weatherForecastType;

	private Date forecastDateTime;

	private Double temperatureValue;

}
