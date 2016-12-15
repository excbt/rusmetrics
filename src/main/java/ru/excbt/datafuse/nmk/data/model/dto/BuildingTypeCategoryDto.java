/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.dto;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.12.2016
 * 
 */
public class BuildingTypeCategoryDto {

	private String keyname;

	private String caption;

	private String description;

	private String buildingType;

	private String parentCategory;

	private Integer orderIdx;

	public String getKeyname() {
		return keyname;
	}

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

}
