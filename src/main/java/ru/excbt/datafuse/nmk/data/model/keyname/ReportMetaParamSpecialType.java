package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name="report_meta_param_special_type")
public class ReportMetaParamSpecialType extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1513108600723081299L;

	@Column(name="caption")
	private String caption;
	
	@Column(name="special_type_name")
	private String name;
	
	@Column(name="special_type_description")
	private String description;

	@Column(name="special_type_comment")
	private String comment;	
	
	@Version
	private int version;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	
}
