package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "weather_forecast")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class WeatherForecast extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 264033612291164599L;

	@Column(name = "weather_place_id", insertable = false, updatable = false)
	private Long weatherPlaceId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "weather_place_id")
	private WeatherPlace weatherPlace;

	@Column(name = "weather_provider")
	private String weatherProvider;

	@Column(name = "weather_forecast_type")
	private String weatherForecastType;

	@Column(name = "forecast_date_time")
	private Date forecastDateTime;

	@Column(name = "temperature_value")
	private BigDecimal temperatureValue;

	@Column(name = "request_id")
	private Long requestId;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
