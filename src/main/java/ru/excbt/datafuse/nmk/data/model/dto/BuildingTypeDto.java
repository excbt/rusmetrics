/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.dto;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2016
 * 
 */
public class BuildingTypeDto {

	private String keyname;

	private String caption;

	private String description;

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

	public Integer getOrderIdx() {
		return orderIdx;
	}

	public void setOrderIdx(Integer orderIdx) {
		this.orderIdx = orderIdx;
	}

}
