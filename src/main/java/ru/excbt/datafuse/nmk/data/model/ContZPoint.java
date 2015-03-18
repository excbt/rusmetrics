package ru.excbt.datafuse.nmk.data.model;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="cont_zpoint")
@EntityListeners({AuditingEntityListener.class})
public class ContZPoint extends AbstractAuditableEntity<SystemUser, Long>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@OneToOne 
	@JoinColumn(name = "cont_object_id")
	@JsonIgnore
	private ContObject contObject;

	
	@OneToOne 
	@JoinColumn(name = "cont_service_type")
	private ContServiceType contServiceType;
	
	@Column(name="custom_service_name")
	private String customServiceName;
	
	@Column(name="start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column(name="end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@OneToMany (fetch = FetchType.EAGER)
    @JoinTable(name="cont_zpoint_device",
    joinColumns=@JoinColumn(name="cont_zpoint_id"),
    inverseJoinColumns=@JoinColumn(name="device_object_id"))	
	private Collection<DeviceObject> deviceObjects;
	
	

	public ContObject getContObject() {
		return contObject;
	}


	public void setContObject(ContObject contObject) {
		this.contObject = contObject;
	}



	public String getCustomServiceName() {
		return customServiceName;
	}


	public void setCustomServiceName(String customServiceName) {
		this.customServiceName = customServiceName;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}



	public Collection<DeviceObject> getDeviceObjects() {
		return deviceObjects;
	}


	public void setDeviceObjects(Collection<DeviceObject> deviceObjects) {
		this.deviceObjects = deviceObjects;
	}


	public ContServiceType getContServiceType() {
		return contServiceType;
	}


	public void setContServiceType(ContServiceType contServiceType) {
		this.contServiceType = contServiceType;
	}	
	
	
}
