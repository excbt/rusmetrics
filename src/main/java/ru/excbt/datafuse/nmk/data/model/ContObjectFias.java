package ru.excbt.datafuse.nmk.data.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cont_object_fias")
@DynamicUpdate
public class ContObjectFias extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4834456607858555535L;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_object_id", insertable = false, updatable = false)
	@JsonIgnore
	private ContObject contObject;

	@Column(name = "cont_object_id")
	private Long contObjectId;

	@Column(name = "fias_uuid")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID fiasUUID;

	@Column(name = "fias_full_address")
	private String fiasFullAddress;

	@Column(name = "geo_json")
	@Type(type = "StringJsonObject")
	private String geoJson;

	@Version
	private int version;

	public Long getContObjectId() {
		return contObjectId;
	}

	public void setContObjectId(Long contObjectId) {
		this.contObjectId = contObjectId;
	}

	public UUID getFiasUUID() {
		return fiasUUID;
	}

	public void setFiasUUID(UUID fiasUUID) {
		this.fiasUUID = fiasUUID;
	}

	public String getFiasFullAddress() {
		return fiasFullAddress;
	}

	public void setFiasFullAddress(String fiasFullAddress) {
		this.fiasFullAddress = fiasFullAddress;
	}

	public String getGeoJson() {
		return geoJson;
	}

	public void setGeoJson(String geoJson) {
		this.geoJson = geoJson;
	}

	public ContObject getContObject() {
		return contObject;
	}

}