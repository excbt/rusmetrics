package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "weather_place")
public class WeatherPlace extends AbstractAuditableModel {

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

	@Column(name = "geo_lat")
	private BigDecimal geoLat;

	@Column(name = "geo_lon")
	private BigDecimal geoLon;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@Column(name = "fias_uuid")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID fiasUuid;

	@Column(name = "fias_sc_name")
	private String fiasScName;

	@Column(name = "fias_level")
	private Integer fiasLevel;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getPlaceShortName() {
		return placeShortName;
	}

	public void setPlaceShortName(String placeShortName) {
		this.placeShortName = placeShortName;
	}

	public String getPlaceShortName2() {
		return placeShortName2;
	}

	public void setPlaceShortName2(String placeShortName2) {
		this.placeShortName2 = placeShortName2;
	}

	public String getPlaceShortName3() {
		return placeShortName3;
	}

	public void setPlaceShortName3(String placeShortName3) {
		this.placeShortName3 = placeShortName3;
	}

	public String getPlaceFullName() {
		return placeFullName;
	}

	public void setPlaceFullName(String placeFullName) {
		this.placeFullName = placeFullName;
	}

	public String getPlaceDescription() {
		return placeDescription;
	}

	public void setPlaceDescription(String placeDescription) {
		this.placeDescription = placeDescription;
	}

	public String getPlaceComment() {
		return placeComment;
	}

	public void setPlaceComment(String placeComment) {
		this.placeComment = placeComment;
	}

	public String getProviderPlaceId() {
		return providerPlaceId;
	}

	public void setProviderPlaceId(String providerPlaceId) {
		this.providerPlaceId = providerPlaceId;
	}

	public String getProviderPlaceId1() {
		return providerPlaceId1;
	}

	public void setProviderPlaceId1(String providerPlaceId1) {
		this.providerPlaceId1 = providerPlaceId1;
	}

	public String getProviderPlaceId2() {
		return providerPlaceId2;
	}

	public void setProviderPlaceId2(String providerPlaceId2) {
		this.providerPlaceId2 = providerPlaceId2;
	}

	public String getProviderPlaceId3() {
		return providerPlaceId3;
	}

	public void setProviderPlaceId3(String providerPlaceId3) {
		this.providerPlaceId3 = providerPlaceId3;
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

	public String getFiasScName() {
		return fiasScName;
	}

	public void setFiasScName(String fiasScName) {
		this.fiasScName = fiasScName;
	}

	public Integer getFiasLevel() {
		return fiasLevel;
	}

	public void setFiasLevel(Integer fiasLevel) {
		this.fiasLevel = fiasLevel;
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