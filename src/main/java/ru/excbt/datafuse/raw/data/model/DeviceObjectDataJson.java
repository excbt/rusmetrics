package ru.excbt.datafuse.raw.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import ru.excbt.datafuse.hibernate.types.StringJsonUserType;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name="device_object_data_json")
@TypeDefs( {@TypeDef( name= "StringJsonObject", typeClass = StringJsonUserType.class)})
public class DeviceObjectDataJson implements Serializable {

 
	/**
	 * 
	 */
	private static final long serialVersionUID = 8976999618636726672L;

	@Id
	@Column(name = "id")
	private Long id;
	
	@Column(name="import_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date importDate;
	
	@Column(name="device_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date deviceDate;
	
	@Column(name="data_json")
	@Type(type = "StringJsonObject")
	@JsonIgnore // We don't want to pass this field to client
	private String dataJson;
	
	@Column(name="device_data_type")
	private String deviceDataType;
	
	@Column(name="device_object_id")
	private Long deviceObjectId;
	
	@Column(name="time_detail_type")
	private String timeDetailType;
	
	@Version
	private int version;

	public Long getId() {
		return id;
	}

	public Date getImportDate() {
		return importDate;
	}

	public Date getDeviceDate() {
		return deviceDate;
	}

	public String getDataJson() {
		return dataJson;
	}

	public String getDeviceDataType() {
		return deviceDataType;
	}

	public String getTimeDetailType() {
		return timeDetailType;
	}

	public int getVersion() {
		return version;
	}

	public Long getDeviceObjectId() {
		return deviceObjectId;
	}

	
}
