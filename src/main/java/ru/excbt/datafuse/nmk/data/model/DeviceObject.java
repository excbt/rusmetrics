package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name="device_object")
public class DeviceObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "device_object", sequenceName = "seq_global_id", allocationSize = 1)	
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_object")
	@Column
	private Long id;		
	
	@OneToOne 
	@JoinColumn(name = "device_model_id")	
	private DeviceModel deviceModel;
	
	@Column(name = "device_object_number")
	private String number;
	
	@Column(name = "ex_code")
	private String exCode;

	@Column(name = "ex_system")
	private String exSystem;
	
	@Version
	private int version;	
	
	@Embedded
	@JsonIgnore
	private RowAudit rowAudit;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
