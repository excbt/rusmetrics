package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "device_data_type")
public class DeviceDataType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "keyname")
	private String keyname;

	@Column(name = "caption")
	private String caption;
	
	@Column(name = "device_data_type_name")
	private String dataTypeName;

	@Column(name = "device_data_type_comment")
	private String dataTypeComment;

	@Column(name = "ex_code")
	private String exCode;
	
	@Version
	private int version;	
	
	@Embedded
	@JsonIgnore
	private RowAudit rowAudit;	
	
	public String getKeyname() {
		return keyname;
	}

	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

	public String getDataTypeName() {
		return dataTypeName;
	}

	public void setDataTypeName(String dataTypeName) {
		this.dataTypeName = dataTypeName;
	}

	public String getDataTypeComment() {
		return dataTypeComment;
	}

	public void setDataTypeComment(String dataTypeComment) {
		this.dataTypeComment = dataTypeComment;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getExCode() {
		return exCode;
	}

	public void setExCode(String exCode) {
		this.exCode = exCode;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public RowAudit getRowAudit() {
		return rowAudit;
	}

	public void setRowAudit(RowAudit rowAudit) {
		this.rowAudit = rowAudit;
	}
	
}
