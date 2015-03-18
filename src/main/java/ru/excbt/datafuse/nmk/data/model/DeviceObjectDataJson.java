package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.hibernate.types.StringJsonUserType;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name="device_object_data_json")
@TypeDefs( {@TypeDef( name= "StringJsonObject", typeClass = StringJsonUserType.class)})
@EntityListeners({AuditingEntityListener.class})
public class DeviceObjectDataJson extends AbstractAuditableEntity<SystemUser, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
 
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
	
	@OneToOne (fetch = FetchType.LAZY) 
	@JoinColumn(name = "device_object_id")
	@JsonIgnore
	private DeviceObject deviceObject;
	
	@Column(name="time_detail_type")
	private String timeDetailType;
	
	@Version
	private int version;

	public Date getImportDate() {
		return importDate;
	}

	public void setImportDate(Date importDate) {
		this.importDate = importDate;
	}

	public Date getDeviceDate() {
		return deviceDate;
	}

	public void setDeviceDate(Date deviceDate) {
		this.deviceDate = deviceDate;
	}

	public String getDataJson() {
		return dataJson;
	}

	public void setDataJson(String dataJson) {
		this.dataJson = dataJson;
	}

	public String getDeviceDataType() {
		return deviceDataType;
	}

	public void setDeviceDataType(String deviceDataType) {
		this.deviceDataType = deviceDataType;
	}

	public DeviceObject getDeviceObject() {
		return deviceObject;
	}

	public void setDeviceObject(DeviceObject deviceObject) {
		this.deviceObject = deviceObject;
	}

	public String getTimeDetailType() {
		return timeDetailType;
	}

	public void setTimeDetailType(String timeDetailType) {
		this.timeDetailType = timeDetailType;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
}
