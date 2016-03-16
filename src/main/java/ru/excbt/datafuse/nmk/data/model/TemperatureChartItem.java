package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "temperature_chart_item")
public class TemperatureChartItem extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5278021673344286068L;

	@Column(name = "temperature_chart_id")
	private Long temperatureChartId;

	@Column(name = "t_ambience")
	private BigDecimal tAmbience;

	@Column(name = "t_in")
	private BigDecimal tIn;

	@Column(name = "t_out")
	private BigDecimal tOut;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public Long getTemperatureChartId() {
		return temperatureChartId;
	}

	public void setTemperatureChartId(Long temperatureChartId) {
		this.temperatureChartId = temperatureChartId;
	}

	public BigDecimal gettAmbience() {
		return tAmbience;
	}

	public void settAmbience(BigDecimal tAmbience) {
		this.tAmbience = tAmbience;
	}

	public BigDecimal gettIn() {
		return tIn;
	}

	public void settIn(BigDecimal tIn) {
		this.tIn = tIn;
	}

	public BigDecimal gettOut() {
		return tOut;
	}

	public void settOut(BigDecimal tOut) {
		this.tOut = tOut;
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