package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "temperature_chart_item")
public class TemperatureChartItem extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5278021673344286068L;

	@Column(name = "temperature_chart_id", insertable = false, updatable = false)
	private Long temperatureChartId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "temperature_chart_id")
	private TemperatureChart temperatureChart;

	@Column(name = "t_ambience")
	private BigDecimal t_Ambience;

	@Column(name = "t_in")
	private BigDecimal t_In;

	@Column(name = "t_out")
	private BigDecimal t_Out;

	@Column(name = "item_comment")
	private String itemComment;

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

	public TemperatureChart getTemperatureChart() {
		return temperatureChart;
	}

	public void setTemperatureChart(TemperatureChart temperatureChart) {
		this.temperatureChart = temperatureChart;
	}

	public BigDecimal getT_Ambience() {
		return t_Ambience;
	}

	public void setT_Ambience(BigDecimal t_Ambience) {
		this.t_Ambience = t_Ambience;
	}

	public BigDecimal getT_In() {
		return t_In;
	}

	public void setT_In(BigDecimal t_In) {
		this.t_In = t_In;
	}

	public BigDecimal getT_Out() {
		return t_Out;
	}

	public void setT_Out(BigDecimal t_Out) {
		this.t_Out = t_Out;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public String getItemComment() {
		return itemComment;
	}

	public void setItemComment(String itemComment) {
		this.itemComment = itemComment;
	}

}