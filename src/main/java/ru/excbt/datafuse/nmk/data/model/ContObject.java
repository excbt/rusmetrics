package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;
import ru.excbt.datafuse.nmk.data.model.markers.ExCodeObject;
import ru.excbt.datafuse.nmk.data.model.markers.ExSystemObject;
import ru.excbt.datafuse.nmk.data.model.markers.ManualObject;

@Entity
@Table(name = "cont_object")
@DynamicUpdate
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ContObject extends AbstractAuditableModel
		implements ExSystemObject, ExCodeObject, DeletableObjectId, ManualObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2174907606605476227L;

	/**
	 * 
	 */

	@Column(name = "cont_object_name")
	private String name;

	@Column(name = "cont_object_full_name")
	private String fullName;

	@Column(name = "cont_object_full_address")
	private String fullAddress;

	@Column(name = "cont_object_number")
	private String number;

	@Column(name = "cont_object_owner")
	private String owner;

	@Column(name = "owner_contacts")
	private String ownerContacts;

	// @OneToMany (fetch = FetchType.LAZY)
	// @JoinTable(name="cont_device_object",
	// joinColumns=@JoinColumn(name="cont_object_id"),
	// inverseJoinColumns=@JoinColumn(name="device_object_id"))
	// @JsonIgnore
	// private Collection<DeviceObject> deviceObjects;

	@Column(name = "current_setting_mode")
	private String currentSettingMode;

	@Column(name = "setting_mode_mdate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date settingModeMDate;

	@Column(name = "cont_object_description")
	private String description;

	@Column(name = "cont_object_comment")
	private String comment;

	@Column(name = "cont_object_cw_temp")
	private BigDecimal cwTemp;

	@Column(name = "cont_object_heat_area")
	private BigDecimal heatArea;

	@Version
	private int version;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "contObject")
	private List<ContManagement> contManagements = new ArrayList<>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "timezone_def")
	private TimezoneDef timezoneDef;

	@Column(name = "timezone_def", insertable = false, updatable = false)
	private String timezoneDefKeyname;

	/*
	 * TODO check contObjectFias
	 */
	@OneToOne(fetch = FetchType.EAGER, mappedBy = "contObject")
	private ContObjectFias contObjectFias;

	/*
	 * TODO check contObjectGeo
	 */
	@OneToOne(fetch = FetchType.EAGER, mappedBy = "contObject")
	private ContObjectGeoPos contObjectGeo;

	@Column(name = "ex_system")
	@JsonIgnore
	private String exSystemKeyname;

	@Column(name = "ex_code")
	@JsonIgnore
	private String exCode;

	@Column(name = "deleted")
	private int deleted;

	@Column(name = "is_manual")
	private Boolean isManual;

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

	// public Collection<DeviceObject> getDeviceObjects() {
	// return deviceObjects;
	// }
	//
	// public void setDeviceObjects(Collection<DeviceObject> deviceObjects) {
	// this.deviceObjects = deviceObjects;
	// }

	public String getCurrentSettingMode() {
		return currentSettingMode;
	}

	public void setCurrentSettingMode(String currentSettingMode) {
		this.currentSettingMode = currentSettingMode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getCwTemp() {
		return cwTemp;
	}

	public void setCwTemp(BigDecimal cwTemp) {
		this.cwTemp = cwTemp;
	}

	public BigDecimal getHeatArea() {
		return heatArea;
	}

	public void setHeatArea(BigDecimal heatArea) {
		this.heatArea = heatArea;
	}

	public List<ContManagement> getContManagements() {
		return contManagements;
	}

	public void setContManagements(List<ContManagement> contManagements) {
		this.contManagements = contManagements;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public TimezoneDef getTimezoneDef() {
		return timezoneDef;
	}

	public void setTimezoneDef(TimezoneDef timezoneDef) {
		this.timezoneDef = timezoneDef;
	}

	public Date getSettingModeMDate() {
		return settingModeMDate;
	}

	public void setSettingModeMDate(Date settingModeMDate) {
		this.settingModeMDate = settingModeMDate;
	}

	public ContObjectFias getContObjectFias() {
		return contObjectFias;
	}

	public ContObjectGeoPos getContObjectGeo() {
		return contObjectGeo;
	}

	public void setContObjectFias(ContObjectFias contObjectFias) {
		this.contObjectFias = contObjectFias;
	}

	public void setContObjectGeo(ContObjectGeoPos contObjectGeo) {
		this.contObjectGeo = contObjectGeo;
	}

	@Override
	public String getExSystemKeyname() {
		return exSystemKeyname;
	}

	@Override
	public String getExCode() {
		return exCode;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	@Override
	public Boolean getIsManual() {
		return isManual;
	}

	public void setIsManual(Boolean isManual) {
		this.isManual = isManual;
	}

	public String getTimezoneDefKeyname() {
		return timezoneDefKeyname;
	}

	public void setTimezoneDefKeyname(String timezoneDefKeyname) {
		this.timezoneDefKeyname = timezoneDefKeyname;
	}

	public void setExSystemKeyname(String exSystemKeyname) {
		this.exSystemKeyname = exSystemKeyname;
	}

	public void setExCode(String exCode) {
		this.exCode = exCode;
	}

}
