package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "local_place")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalPlace extends AbstractAuditableModel {

	public static class LocalPlaceInfo {
		private final Long id;
		private final String localPlaceName;
		private final Long weatherPlaceId;
		private final String weatherPlaceName;

		public LocalPlaceInfo(LocalPlace localPlace) {
			this.id = localPlace.getId();
			this.localPlaceName = localPlace.getLocalPlaceName();
			this.weatherPlaceId = localPlace.getWeatherPlaceId();
			this.weatherPlaceName = localPlace.getWeatherPlace().getPlaceName();
		}

		public String getLocalPlaceName() {
			return localPlaceName;
		}

		public Long getId() {
			return id;
		}

		public Long getWeatherPlaceId() {
			return weatherPlaceId;
		}

		public String getWeatherPlaceName() {
			return weatherPlaceName;
		}

		public boolean haveWeatherPlace() {
			return this.weatherPlaceId != null;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2419941585129491457L;

	@Column(name = "local_place_name")
	private String localPlaceName;

	@Column(name = "local_place_short_name")
	private String localPlaceShortName;

	@Column(name = "local_place_short_name2")
	private String localPlaceShortName2;

	@Column(name = "local_place_short_name3")
	private String localPlaceShortName3;

	@Column(name = "local_place_full_name")
	private String localPlaceFullName;

	@Column(name = "local_place_description")
	private String localPlaceDescription;

	@Column(name = "local_place_comment")
	private String localPlaceComment;

	@Column(name = "weather_place_id", insertable = false, updatable = false)
	private Long weatherPlaceId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "weather_place_id")
	private WeatherPlace weatherPlace;

	@Column(name = "geo_lat")
	private BigDecimal geoLat;

	@Column(name = "geo_lon")
	private BigDecimal geoLon;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@Column(name = "fias_uuid")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID fiasUuid;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public String getLocalPlaceName() {
		return localPlaceName;
	}

	public void setLocalPlaceName(String localPlaceName) {
		this.localPlaceName = localPlaceName;
	}

	public String getLocalPlaceShortName() {
		return localPlaceShortName;
	}

	public void setLocalPlaceShortName(String localPlaceShortName) {
		this.localPlaceShortName = localPlaceShortName;
	}

	public String getLocalPlaceShortName2() {
		return localPlaceShortName2;
	}

	public void setLocalPlaceShortName2(String localPlaceShortName2) {
		this.localPlaceShortName2 = localPlaceShortName2;
	}

	public String getLocalPlaceShortName3() {
		return localPlaceShortName3;
	}

	public void setLocalPlaceShortName3(String localPlaceShortName3) {
		this.localPlaceShortName3 = localPlaceShortName3;
	}

	public String getLocalPlaceFullName() {
		return localPlaceFullName;
	}

	public void setLocalPlaceFullName(String localPlaceFullName) {
		this.localPlaceFullName = localPlaceFullName;
	}

	public String getLocalPlaceDescription() {
		return localPlaceDescription;
	}

	public void setLocalPlaceDescription(String localPlaceDescription) {
		this.localPlaceDescription = localPlaceDescription;
	}

	public String getLocalPlaceComment() {
		return localPlaceComment;
	}

	public void setLocalPlaceComment(String localPlaceComment) {
		this.localPlaceComment = localPlaceComment;
	}

	public Long getWeatherPlaceId() {
		return weatherPlaceId;
	}

	public void setWeatherPlaceId(Long weatherPlaceId) {
		this.weatherPlaceId = weatherPlaceId;
	}

	public BigDecimal getGeoLat() {
		return geoLat;
	}

	public void setGeoLat(BigDecimal geoLat) {
		this.geoLat = geoLat;
	}

	public BigDecimal getGeoLon() {
		return geoLon;
	}

	public void setGeoLon(BigDecimal geoLon) {
		this.geoLon = geoLon;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public UUID getFiasUuid() {
		return fiasUuid;
	}

	public void setFiasUuid(UUID fiasUuid) {
		this.fiasUuid = fiasUuid;
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

	public WeatherPlace getWeatherPlace() {
		return weatherPlace;
	}

	public void setWeatherPlace(WeatherPlace weatherPlace) {
		this.weatherPlace = weatherPlace;
	}

}