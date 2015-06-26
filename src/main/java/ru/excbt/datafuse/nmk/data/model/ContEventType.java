package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "cont_event_type")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContEventType extends AbstractPersistableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "keyname")
	private String keyname;

	@Column(name = "caption")
	private String caption;

	@Column(name = "cont_event_type_name")
	private String name;

	@Column(name = "cont_event_type_comment")
	private String comment;

	@Column(name = "cont_event_type_description")
	private String description;

	@Version
	private int version;

	@Column(name = "parent_id")
	private Long parentId;

	@Column(name = "cont_event_level")
	private Integer contEventLevel;

	@Column(name = "cont_event_category")
	private String contEventCategory;

	@Column(name = "reverse_id")
	private Long reverseId;

	@Column(name = "is_base_event")
	private Boolean isBaseEvent;

	@Column(name = "is_critical_event")
	private Boolean isCriticalEvent;

	@Column(name = "is_scalar_event")
	private Boolean isScalarEvent;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Integer getContEventLevel() {
		return contEventLevel;
	}

	public void setContEventLevel(Integer contEventLevel) {
		this.contEventLevel = contEventLevel;
	}

	public String getContEventCategory() {
		return contEventCategory;
	}

	public void setContEventCategory(String contEventCategory) {
		this.contEventCategory = contEventCategory;
	}

	public Long getReverseId() {
		return reverseId;
	}

	public void setReverseId(Long reverseId) {
		this.reverseId = reverseId;
	}

	public Boolean getIsBaseEvent() {
		return isBaseEvent;
	}

	public void setIsBaseEvent(Boolean isBaseEvent) {
		this.isBaseEvent = isBaseEvent;
	}

	public Boolean getIsCriticalEvent() {
		return isCriticalEvent;
	}

	public void setIsCriticalEvent(Boolean isCriticalEvent) {
		this.isCriticalEvent = isCriticalEvent;
	}

	public Boolean getIsScalarEvent() {
		return isScalarEvent;
	}

	public void setIsScalarEvent(Boolean isScalarEvent) {
		this.isScalarEvent = isScalarEvent;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
