package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	private String specialTypeName;
	
	@Column(name="special_type_description")
	@JsonIgnore
	private String specialTypeDescription;

	@Column(name="special_type_comment")
	@JsonIgnore
	private String specialTypeComment;	

	@Column(name="special_type_directory")
	private String specialTypeDirectory;	
	
	@Version
	private int version;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getSpecialTypeName() {
		return specialTypeName;
	}

	public void setSpecialTypeName(String specialTypeName) {
		this.specialTypeName = specialTypeName;
	}

	public String getSpecialTypeDescription() {
		return specialTypeDescription;
	}

	public void setSpecialTypeDescription(String specialTypeDescription) {
		this.specialTypeDescription = specialTypeDescription;
	}

	public String getSpecialTypeComment() {
		return specialTypeComment;
	}

	public void setSpecialTypeComment(String specialTypeComment) {
		this.specialTypeComment = specialTypeComment;
	}

	public String getSpecialTypeDirectory() {
		return specialTypeDirectory;
	}

	public void setSpecialTypeDirectory(String specialTypeDirectory) {
		this.specialTypeDirectory = specialTypeDirectory;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}


}
