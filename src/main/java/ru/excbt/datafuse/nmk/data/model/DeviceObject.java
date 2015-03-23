package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;


@Entity
@Table(name="device_object")
@EntityListeners({AuditingEntityListener.class})
public class DeviceObject extends AbstractAuditableEntity<AuditUser, Long> {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = -199459403017867220L;

	@OneToOne 
	@JoinColumn(name = "device_model_id")
	private DeviceModel deviceModel;
	
	@Column(name = "device_object_number")
	private String number;
	
	@Column(name = "ex_code")
	private String exCode;

	@Column(name = "ex_system")
	private String exSystem;
	
//	@ManyToOne 
//    @JoinTable(name="cont_device_object",
//    joinColumns=@JoinColumn(name="device_object_id"),
//    inverseJoinColumns=@JoinColumn(name="cont_object_id"))
//	@JsonIgnore
//	private ContObject contObject;
	
	@Version
	private int version; 
	
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

//	public ContObject getContObject() {
//		return contObject;
//	}
//
//	public void setContObject(ContObject contObject) {
//		this.contObject = contObject;
//	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}	
	
	
}
