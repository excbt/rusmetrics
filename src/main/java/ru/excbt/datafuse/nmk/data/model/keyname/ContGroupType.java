package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_group_type")
public class ContGroupType extends AbstractKeynameEntity {

	/**
		 * 
		 */
	private static final long serialVersionUID = 8527718550849111235L;

	@Column(name = "caption")
	private String contGroupTypeCaption;

	@Column(name = "type_name")
	private String contGroupTypeName;

	@Column(name = "type_comment")
	private String contGroupTypeComment;

	@Column(name = "type_description")
	private String contGroupTypeDescription;

	@Version
	private int version;

	public String getContGroupTypeCaption() {
		return contGroupTypeCaption;
	}

	public void setContGroupTypeCaption(String contGroupTypeCaption) {
		this.contGroupTypeCaption = contGroupTypeCaption;
	}

	public String getContGroupTypeName() {
		return contGroupTypeName;
	}

	public void setContGroupTypeName(String contGroupTypeName) {
		this.contGroupTypeName = contGroupTypeName;
	}

	public String getContGroupTypeComment() {
		return contGroupTypeComment;
	}

	public void setContGroupTypeComment(String contGroupTypeComment) {
		this.contGroupTypeComment = contGroupTypeComment;
	}

	public String getContGroupTypeDescription() {
		return contGroupTypeDescription;
	}

	public void setContGroupTypeDescription(String contGroupTypeDescription) {
		this.contGroupTypeDescription = contGroupTypeDescription;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
