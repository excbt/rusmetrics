package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

@Entity
@Table(name = "measure_unit")
public class MeasureUnit extends AbstractKeynameEntity implements DeletableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2220706708330783417L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "measure_unit_name")
	private String unitName;

	@Column(name = "measure_unit_comment")
	private String unitComment;

	@Column(name = "measure_unit_description")
	private String unitDescription;

	@Column(name = "measure_category")
	private String measureCategory;

	@Version
	@JsonIgnore
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getUnitComment() {
		return unitComment;
	}

	public void setUnitComment(String unitComment) {
		this.unitComment = unitComment;
	}

	public String getUnitDescription() {
		return unitDescription;
	}

	public void setUnitDescription(String unitDescription) {
		this.unitDescription = unitDescription;
	}

	public String getMeasureCategory() {
		return measureCategory;
	}

	public void setMeasureCategory(String measureCategory) {
		this.measureCategory = measureCategory;
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
