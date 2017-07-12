package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "weather_place")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class WeatherPlace extends AbstractAuditableModel implements DisabledObject, DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = -8897127583985211173L;

	@Column(name = "place_name")
	private String placeName;

	@Column(name = "place_short_name")
	private String placeShortName;

	@Column(name = "place_short_name2")
	private String placeShortName2;

	@Column(name = "place_short_name3")
	private String placeShortName3;

	@Column(name = "place_full_name")
	private String placeFullName;

	@Column(name = "place_description")
	private String placeDescription;

	@Column(name = "place_comment")
	private String placeComment;

	@Column(name = "provider_place_id")
	private String providerPlaceId;

	@Column(name = "provider_place_id1")
	private String providerPlaceId1;

	@Column(name = "provider_place_id2")
	private String providerPlaceId2;

	@Column(name = "provider_place_id3")
	private String providerPlaceId3;

	@Column(name = "geo_lat", columnDefinition = "numeric")
	private Double geoLat;

	@Column(name = "geo_lon", columnDefinition = "numeric")
	private Double geoLon;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@Column(name = "fias_uuid")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID fiasUuid;

	@Column(name = "fias_sc_name")
	private String fiasScName;

	@Column(name = "fias_level")
	private Integer fiasLevel;

	@Column(name = "place_priority")
	private Integer placePriority;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
