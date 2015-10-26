package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.StatusColorObject;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "cont_event_level_color")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContEventLevelColor extends AbstractKeynameEntity implements
		StatusColorObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -406404451942425879L;

	@Column(name = "keyname", insertable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	private ContEventLevelColorKey colorKey;

	@Column(name = "caption")
	private String caption;

	@Column(name = "color_description")
	@JsonIgnore
	private String colorDescription;

	@Column(name = "color_rank")
	private Integer colorRank;

	@Column(name = "is_bad_color")
	private Boolean isBadColor;

	@Column(name = "is_base_color")
	private Boolean isBaseColor;

	@Column(name = "is_critical_color")
	private Boolean isCriticalColor;

	@Column(name = "cont_event_level_from")
	@JsonIgnore	
	private Integer levelFrom;

	@Column(name = "cont_event_level_to")
	@JsonIgnore	
	private Integer levelTo;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getColorDescription() {
		return colorDescription;
	}

	public void setColorDescription(String colorDescription) {
		this.colorDescription = colorDescription;
	}

	public Integer getColorRank() {
		return colorRank;
	}

	public void setColorRank(Integer colorRank) {
		this.colorRank = colorRank;
	}

	public Boolean getIsBadColor() {
		return isBadColor;
	}

	public void setIsBadColor(Boolean isBadColor) {
		this.isBadColor = isBadColor;
	}

	public Boolean getIsBaseColor() {
		return isBaseColor;
	}

	public void setIsBaseColor(Boolean isBaseColor) {
		this.isBaseColor = isBaseColor;
	}

	public Boolean getIsCriticalColor() {
		return isCriticalColor;
	}

	public void setIsCriticalColor(Boolean isCriticalColor) {
		this.isCriticalColor = isCriticalColor;
	}

	public Integer getLevelFrom() {
		return levelFrom;
	}

	public void setLevelFrom(Integer levelFrom) {
		this.levelFrom = levelFrom;
	}

	public Integer getLevelTo() {
		return levelTo;
	}

	public void setLevelTo(Integer levelTo) {
		this.levelTo = levelTo;
	}

	public ContEventLevelColorKey getColorKey() {
		return colorKey;
	}

	public void setColorKey(ContEventLevelColorKey colorKey) {
		this.colorKey = colorKey;
	}

	@Override
	public String getStatusColor() {
		return this.getKeyname();
	}

}
