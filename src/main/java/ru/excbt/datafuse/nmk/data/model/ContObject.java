package ru.excbt.datafuse.nmk.data.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity
@Table(name="cont_object")
@EntityListeners({AuditingEntityListener.class})
public class ContObject extends AbstractAuditableEntity<AuditUser, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="cont_object_name")
	private String name;

	@Column(name="cont_object_full_name")
	private String fullName;
	
	@Column(name="cont_object_full_address")
	private String fullAddress;

	@Column(name="cont_object_number")
	private String number;

	@Column(name="cont_object_owner")
	private String owner;

	@Column(name="owner_contacts")
	private String ownerContacts;

	@OneToMany (fetch = FetchType.LAZY)
    @JoinTable(name="cont_device_object",
    joinColumns=@JoinColumn(name="cont_object_id"),
    inverseJoinColumns=@JoinColumn(name="device_object_id"))
	@JsonIgnore
	private Collection<DeviceObject> deviceObjects;	
	
	@Column(name = "current_setting_mode")
	private String currentSettingMode;
	
	@Version
    private int version; 
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwnerContacts() {
		return ownerContacts;
	}

	public void setOwnerContacts(String ownerContacts) {
		this.ownerContacts = ownerContacts;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Collection<DeviceObject> getDeviceObjects() {
		return deviceObjects;
	}

	public void setDeviceObjects(Collection<DeviceObject> deviceObjects) {
		this.deviceObjects = deviceObjects;
	}

	public String getCurrentSettingMode() {
		return currentSettingMode;
	}

	public void setCurrentSettingMode(String currentSettingMode) {
		this.currentSettingMode = currentSettingMode;
	}

	
}
