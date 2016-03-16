package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "temperature_chart")
public class TemperatureChart extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1684076198823551659L;

	@Column(name = "local_place_id")
	private Long localPlaceId;

	@Column(name = "rso_organization_id")
	private Long rsoOrganizationId;

	@Column(name = "chart_title")
	private String chartTitle;

	@Column(name = "is_default")
	private Boolean isDefault;

	@Column(name = "max_t")
	private BigDecimal maxT;

	@Column(name = "min_t")
	private BigDecimal minT;

	@Column(name = "flag_adj_pump")
	private Boolean flagAdjPump;

	@Column(name = "flag_elevator")
	private Boolean flagElevator;

	public Long getLocalPlaceId() {
		return localPlaceId;
	}

	public void setLocalPlaceId(Long localPlaceId) {
		this.localPlaceId = localPlaceId;
	}

	public Long getRsoOrganizationId() {
		return rsoOrganizationId;
	}

	public void setRsoOrganizationId(Long rsoOrganizationId) {
		this.rsoOrganizationId = rsoOrganizationId;
	}

	public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public BigDecimal getMaxT() {
		return maxT;
	}

	public void setMaxT(BigDecimal maxT) {
		this.maxT = maxT;
	}

	public BigDecimal getMinT() {
		return minT;
	}

	public void setMinT(BigDecimal minT) {
		this.minT = minT;
	}

	public Boolean getFlagAdjPump() {
		return flagAdjPump;
	}

	public void setFlagAdjPump(Boolean flagAdjPump) {
		this.flagAdjPump = flagAdjPump;
	}

	public Boolean getFlagElevator() {
		return flagElevator;
	}

	public void setFlagElevator(Boolean flagElevator) {
		this.flagElevator = flagElevator;
	}

}