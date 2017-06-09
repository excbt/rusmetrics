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

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "temperature_chart_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
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

}
