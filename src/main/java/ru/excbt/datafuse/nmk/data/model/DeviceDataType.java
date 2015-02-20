package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "device_data_type")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceDataType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "keyname")
	private String keyname;
	
	@Column(name = "device_data_type_name")
	private String dataTypeName;

	@Column(name = "device_data_type_comment")
	private String dataTypeComment;

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
	
}
