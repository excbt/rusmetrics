/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2016
 * 
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "_building_type_category")
public class BuildingTypeCategory extends AbstractKeynameEntity implements DeletableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3879053027088782119L;

	@Column(name = "keyname")
	private String keyname;

	@Column(name = "caption")
	private String caption;

	@Column(name = "description")
	private String description;

	@Column(name = "building_type")
	private String buildingType;

	@Column(name = "parent_category")
	private String parentCategory;

	@Column(name = "order_idx")
	private Integer orderIdx;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	@Override
	public String getKeyname() {
		return keyname;
	}

	@Override
	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBuildingType() {
		return buildingType;
	}

	public void setBuildingType(String buildingType) {
		this.buildingType = buildingType;
	}

	public String getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
	}

	public Integer getOrderIdx() {
		return orderIdx;
	}

	public void setOrderIdx(Integer orderIdx) {
		this.orderIdx = orderIdx;
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

}