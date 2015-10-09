package ru.excbt.datafuse.nmk.data.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(name = "cont_object_fias")
@JsonInclude(Include.NON_NULL)
public class ContObjectFias extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4834456607858555535L;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_object_id")
	@JsonIgnore
	private ContObject contObject;

	@Column(name = "cont_object_id", insertable = false, updatable = false)
	private Long contObjectId;

	@Column(name = "fias_uuid")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID fiasUUID;

	@Column(name = "fias_full_address")
	@JsonIgnore
	private String fiasFullAddress;

	@Column(name = "city_fias_uuid")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID cityFiasUUID;

	@Column(name = "geo_full_address")
	private String geoFullAddress;

	@Column(name = "short_address_1")
	private String shortAddress1;

	@Column(name = "short_address_2")
	private String shortAddress2;

	@Column(name = "short_address_3")
	private String shortAddress3;

	@Version
	private int version;

	public ContObject getContObject() {
		return contObject;
	}

	public Long getContObjectId() {
		return contObjectId;
	}

	public UUID getFiasUUID() {
		return fiasUUID;
	}

	public String getFiasFullAddress() {
		return fiasFullAddress;
	}

	public String getGeoFullAddress() {
		return geoFullAddress;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public UUID getCityFiasUUID() {
		return cityFiasUUID;
	}

	public String getShortAddress1() {
		return shortAddress1;
	}

	public String getShortAddress2() {
		return shortAddress2;
	}

	public String getShortAddress3() {
		return shortAddress3;
	}

	public int getVersion() {
		return version;
	}

}
