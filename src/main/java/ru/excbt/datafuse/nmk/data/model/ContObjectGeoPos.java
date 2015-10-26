package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "v_cont_object_geo_pos_xy")
@JsonInclude(Include.NON_NULL)
public class ContObjectGeoPos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7360034990378154122L;

	@Id
	@Column(name = "cont_object_id", insertable = false, updatable = false)
	private Long contObjectId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cont_object_id")
	@JsonIgnore
	private ContObject contObject;

	@Column(name = "geo_pos_x")
	private BigDecimal geoPosX;

	@Column(name = "geo_pos_y")
	private BigDecimal geoPosY;

	@Column(name = "city_geo_pos_x")
	private BigDecimal cityGeoPosX;

	@Column(name = "city_geo_pos_y")
	private BigDecimal cityGeoPosY;

	public Long getContObjectId() {
		return contObjectId;
	}

	public ContObject getContObject() {
		return contObject;
	}

	public BigDecimal getGeoPosX() {
		return geoPosX;
	}

	public BigDecimal getGeoPosY() {
		return geoPosY;
	}

	public BigDecimal getCityGeoPosX() {
		return cityGeoPosX;
	}

	public BigDecimal getCityGeoPosY() {
		return cityGeoPosY;
	}
}
