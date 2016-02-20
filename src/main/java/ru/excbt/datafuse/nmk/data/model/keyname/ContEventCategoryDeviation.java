package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_event_category_deviation")
public class ContEventCategoryDeviation extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5097483947950990148L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "deviation_description")
	private String deviationDescription;

	@Column(name = "deviation_comment")
	private String deviationComment;

	@Column(name = "cont_event_category")
	private String contEventCategory;

	@Column(name = "value_unit")
	private String valueUnit;

	@Column(name = "value_unit_caption")
	private String valueUnitCaption;

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

	public String getContEventCategory() {
		return contEventCategory;
	}

	public void setContEventCategory(String contEventCategory) {
		this.contEventCategory = contEventCategory;
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

}