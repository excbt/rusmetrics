package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.domain.IdEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name="device_object")
public class DeviceObject extends IdEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@OneToOne 
	@JoinColumn(name = "device_model_id")
	private DeviceModel deviceModel;
	
	@Column(name = "device_object_number")
	private String number;
	
	@Column(name = "ex_code")
	private String exCode;

	@Column(name = "ex_system")
	private String exSystem;
	
	@ManyToOne 
    @JoinTable(name="device_object_cont",
    joinColumns=@JoinColumn(name="device_object_id"),
    inverseJoinColumns=@JoinColumn(name="cont_object_id"))
	@JsonIgnore
	private ContObject contObject;
	

	public DeviceModel getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(DeviceModel deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getExCode() {
		return exCode;
	}

	public void setExCode(String exCode) {
		this.exCode = exCode;
	}

	public String getExSystem() {
		return exSystem;
	}

	public void setExSystem(String exSystem) {
		this.exSystem = exSystem;
	}

	public ContObject getContObject() {
		return contObject;
	}

	public void setContObject(ContObject contObject) {
		this.contObject = contObject;
	}	
	
	
}
