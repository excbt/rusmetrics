package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_event_category_deviation")
@JsonInclude(Include.NON_NULL)
public class ContEventCategoryDeviation extends AbstractKeynameEntity implements DeletableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5097483947950990148L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "deviation_description")
	private String deviationDescription;

	@JsonIgnore
	@Column(name = "deviation_comment")
	private String deviationComment;

	@Column(name = "cont_event_category")
	private String contEventCategoryKeyname;

	@Column(name = "deviation_order")
	private Integer deviationOrder;

	@Column(name = "value_unit")
	private String valueUnit;

	@Column(name = "value_unit_caption")
	private String valueUnitCaption;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	private int deleted;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDeviationDescription() {
		return deviationDescription;
	}

	public void setDeviationDescription(String deviationDescription) {
		this.deviationDescription = deviationDescription;
	}

	public String getDeviationComment() {
		return deviationComment;
	}

	public void setDeviationComment(String deviationComment) {
		this.deviationComment = deviationComment;
	}

	public String getContEventCategoryKeyname() {
		return contEventCategoryKeyname;
	}

	public void setContEventCategoryKeyname(String contEventCategoryKeyname) {
		this.contEventCategoryKeyname = contEventCategoryKeyname;
	}

	public Integer getDeviationOrder() {
		return deviationOrder;
	}

	public void setDeviationOrder(Integer deviationOrder) {
		this.deviationOrder = deviationOrder;
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

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

}