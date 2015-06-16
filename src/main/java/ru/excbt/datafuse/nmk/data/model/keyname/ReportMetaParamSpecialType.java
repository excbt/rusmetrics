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

	@Column(name="special_type_directory_url")
	private String specialTypeDirectoryUrl;	
	
	@Column(name="special_type_directory_key")
	private String specialTypeDirectoryKey;	
	
	@Column(name="special_type_directory_caption")
	private String specialTypeDirectoryCaption;	

	@Column(name="special_type_directory_value")
	private String specialTypeDirectoryValue;	
	
	@Column(name="special_type_field1")
	private String specialTypeField1;

	@Column(name="special_type_field2")
	private String specialTypeField2;
	
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

	public String getSpecialTypeField1() {
		return specialTypeField1;
	}

	public void setSpecialTypeField1(String specialTypeField1) {
		this.specialTypeField1 = specialTypeField1;
	}

	public String getSpecialTypeField2() {
		return specialTypeField2;
	}

	public void setSpecialTypeField2(String specialTypeField2) {
		this.specialTypeField2 = specialTypeField2;
	}

	public String getSpecialTypeDirectoryUrl() {
		return specialTypeDirectoryUrl;
	}

	public void setSpecialTypeDirectoryUrl(String specialTypeDirectoryUrl) {
		this.specialTypeDirectoryUrl = specialTypeDirectoryUrl;
	}

	public String getSpecialTypeDirectoryKey() {
		return specialTypeDirectoryKey;
	}

	public void setSpecialTypeDirectoryKey(String specialTypeDirectoryKey) {
		this.specialTypeDirectoryKey = specialTypeDirectoryKey;
	}

	public String getSpecialTypeDirectoryCaption() {
		return specialTypeDirectoryCaption;
	}

	public void setSpecialTypeDirectoryCaption(String specialTypeDirectoryCaption) {
		this.specialTypeDirectoryCaption = specialTypeDirectoryCaption;
	}

	public String getSpecialTypeDirectoryValue() {
		return specialTypeDirectoryValue;
	}

	public void setSpecialTypeDirectoryValue(String specialTypeDirectoryValue) {
		this.specialTypeDirectoryValue = specialTypeDirectoryValue;
	}


}
