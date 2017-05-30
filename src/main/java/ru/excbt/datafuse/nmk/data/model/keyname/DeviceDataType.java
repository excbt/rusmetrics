package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_data_type")
public class DeviceDataType extends AbstractKeynameEntity {


	/**
	 *
	 */
	private static final long serialVersionUID = -3395634606730299938L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "data_type_name")
	private String dataTypeName;

	@Column(name = "data_type_comment")
	private String dataTypeComment;

	@Column(name = "ex_code")
	private String exCode;

	@Version
	private int version;

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


}
