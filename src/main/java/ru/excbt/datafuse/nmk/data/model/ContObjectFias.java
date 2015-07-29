package ru.excbt.datafuse.nmk.data.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "cont_object_fias")
@JsonInclude(Include.NON_NULL)
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
	@JsonIgnore
	private String fiasFullAddress;

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

}
