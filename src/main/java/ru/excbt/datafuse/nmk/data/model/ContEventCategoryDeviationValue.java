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
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventCategoryDeviation;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_event_category_deviation_value")
public class ContEventCategoryDeviationValue extends AbstractAuditableModel {

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

	@Column(name = "value_man")
	private BigDecimal valueMan;

	@Column(name = "value_unit")
	private String valueUnit;

	@Column(name = "value_unit_caption")
	private String valueUnitCaption;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public String getContEventCategoryDeviationKeyname() {
		return contEventCategoryDeviationKeyname;
	}

	public void setContEventCategoryDeviationKeyname(String contEventCategoryDeviationKeyname) {
		this.contEventCategoryDeviationKeyname = contEventCategoryDeviationKeyname;
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

	public BigDecimal getValueMan() {
		return valueMan;
	}

	public void setValueMan(BigDecimal valueMan) {
		this.valueMan = valueMan;
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

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public ContEventCategoryDeviation getContEventCategoryDeviation() {
		return contEventCategoryDeviation;
	}

	public void setContEventCategoryDeviation(ContEventCategoryDeviation contEventCategoryDeviation) {
		this.contEventCategoryDeviation = contEventCategoryDeviation;
	}

}