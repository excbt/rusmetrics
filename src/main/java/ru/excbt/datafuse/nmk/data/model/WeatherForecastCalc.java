package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "weather_forecast_calc")
public class WeatherForecastCalc extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8620072123402756177L;

	@Column(name = "weather_place_id", insertable = false, updatable = false)
	private Long weatherPlaceId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "weather_place_id")
	private WeatherPlace weatherPlace;

	@Column(name = "weather_forecast_type")
	private String weatherForecastType;

	@Temporal(TemporalType.DATE)
	@Column(name = "forecast_date")
	private Date forecastDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "forecast_date_time")
	private Date forecastDateTime;

	@Column(name = "calc_type")
	private String calcType;

	@Column(name = "calc_value")
	private BigDecimal calcValue;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public Long getWeatherPlaceId() {
		return weatherPlaceId;
	}

	public void setWeatherPlaceId(Long weatherPlaceId) {
		this.weatherPlaceId = weatherPlaceId;
	}

	public WeatherPlace getWeatherPlace() {
		return weatherPlace;
	}

	public void setWeatherPlace(WeatherPlace weatherPlace) {
		this.weatherPlace = weatherPlace;
	}

	public String getWeatherForecastType() {
		return weatherForecastType;
	}

	public void setWeatherForecastType(String weatherForecastType) {
		this.weatherForecastType = weatherForecastType;
	}

	public Date getForecastDate() {
		return forecastDate;
	}

	public void setForecastDate(Date forecastDate) {
		this.forecastDate = forecastDate;
	}

	public Date getForecastDateTime() {
		return forecastDateTime;
	}

	public void setForecastDateTime(Date forecastDateTime) {
		this.forecastDateTime = forecastDateTime;
	}

	public String getCalcType() {
		return calcType;
	}

	public void setCalcType(String calcType) {
		this.calcType = calcType;
	}

	public BigDecimal getCalcValue() {
		return calcValue;
	}

	public void setCalcValue(BigDecimal calcValue) {
		this.calcValue = calcValue;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

}