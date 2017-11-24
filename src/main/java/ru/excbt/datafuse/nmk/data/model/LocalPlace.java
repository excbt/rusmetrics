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

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.domain.PersistableBuilder;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "local_place")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class LocalPlace extends AbstractAuditableModel implements PersistableBuilder<LocalPlace, Long> {

    @Getter
	public static class LocalPlaceInfo {
		private final Long id;
		private final String localPlaceName;
		private final Long weatherPlaceId;
		private final String weatherPlaceName;

		public LocalPlaceInfo(LocalPlace localPlace) {
			this.id = localPlace.getId();
			this.localPlaceName = localPlace.getLocalPlaceName();
			this.weatherPlaceId = localPlace.getWeatherPlaceId();
			this.weatherPlaceName = localPlace.getWeatherPlaceId() != null ? localPlace.getWeatherPlace().getPlaceName()
					: null;
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

	@Column(name = "geo_lat", columnDefinition = "numeric")
	private Double geoLat;

	@Column(name = "geo_lon", columnDefinition = "numeric")
	private Double geoLon;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@Column(name = "fias_uuid", columnDefinition = "uuid")
	private UUID fiasUuid;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;


}
