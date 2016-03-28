package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

@Entity
@Table(name = "device_metadata_type")
public class DeviceMetadataType extends AbstractKeynameEntity implements DisabledObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2899139629392944981L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "meta_type_name")
	private String typeName;

	@Column(name = "meta_type_comment")
	private String typeComment;

	@Column(name = "meta_type_description")
	private String typeDescription;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeComment() {
		return typeComment;
	}

	public void setTypeComment(String typeComment) {
		this.typeComment = typeComment;
	}

	public String getTypeDescription() {
		return typeDescription;
	}

	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}

	@Override
	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}
}
