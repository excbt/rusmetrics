package ru.excbt.datafuse.nmk.data.model.keyname;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_event_category_deviation_value")
@JsonInclude(Include.NON_NULL)
public class ContEventCategoryDeviationValue extends AbstractKeynameEntity implements DeletableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3846083834317785457L;

	@Column(name = "cont_event_category_deviation")
	private String contEventCategoryDeviationKeyname;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cont_event_category_deviation", insertable = false, updatable = false)
	private ContEventCategoryDeviation contEventCategoryDeviation;

	@Column(name = "value_name")
	private String valueName;

	@Column(name = "caption")
	private String caption;

	@Column(name = "value_description")
	private String valueDescription;

	@Column(name = "value_comment")
	private String valueComment;

	@Column(name = "value_min")
	private BigDecimal valueMin;

	@Column(name = "value_min2")
	private BigDecimal valueMin2;

	@Column(name = "value_max")
	private BigDecimal valueMax;

	@Column(name = "value_max2")
	private BigDecimal valueMax2;

	@Column(name = "value_unit")
	private String valueUnit;

	@Column(name = "value_unit_caption")
	private String valueUnitCaption;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	@Column(name = "value_time_begin")
	private String valueTimeBegin;

	@Column(name = "value_time_end")
	private String valueTimeEnd;

	@Column(name = "value_order")
	private Integer valueOrder;

	public String getContEventCategoryDeviationKeyname() {
		return contEventCategoryDeviationKeyname;
	}

	public void setContEventCategoryDeviationKeyname(String contEventCategoryDeviationKeyname) {
		this.contEventCategoryDeviationKeyname = contEventCategoryDeviationKeyname;
	}

	public ContEventCategoryDeviation getContEventCategoryDeviation() {
		return contEventCategoryDeviation;
	}

	public void setContEventCategoryDeviation(ContEventCategoryDeviation contEventCategoryDeviation) {
		this.contEventCategoryDeviation = contEventCategoryDeviation;
	}

	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getValueDescription() {
		return valueDescription;
	}

	public void setValueDescription(String valueDescription) {
		this.valueDescription = valueDescription;
	}

	public String getValueComment() {
		return valueComment;
	}

	public void setValueComment(String valueComment) {
		this.valueComment = valueComment;
	}

	public BigDecimal getValueMin() {
		return valueMin;
	}

	public void setValueMin(BigDecimal valueMin) {
		this.valueMin = valueMin;
	}

	public BigDecimal getValueMin2() {
		return valueMin2;
	}

	public void setValueMin2(BigDecimal valueMin2) {
		this.valueMin2 = valueMin2;
	}

	public BigDecimal getValueMax() {
		return valueMax;
	}

	public void setValueMax(BigDecimal valueMax) {
		this.valueMax = valueMax;
	}

	public BigDecimal getValueMax2() {
		return valueMax2;
	}

	public void setValueMax2(BigDecimal valueMax2) {
		this.valueMax2 = valueMax2;
	}

	public String getValueUnit() {
		return valueUnit;
	}

	public void setValueUnit(String valueUnit) {
		this.valueUnit = valueUnit;
	}

	public String getValueUnitCaption() {
		return valueUnitCaption;
	}

	public void setValueUnitCaption(String valueUnitCaption) {
		this.valueUnitCaption = valueUnitCaption;
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

	public String getValueTimeBegin() {
		return valueTimeBegin;
	}

	public void setValueTimeBegin(String valueTimeBegin) {
		this.valueTimeBegin = valueTimeBegin;
	}

	public String getValueTimeEnd() {
		return valueTimeEnd;
	}

	public void setValueTimeEnd(String valueTimeEnd) {
		this.valueTimeEnd = valueTimeEnd;
	}

	public Integer getValueOrder() {
		return valueOrder;
	}

	public void setValueOrder(Integer valueOrder) {
		this.valueOrder = valueOrder;
	}

}