package ru.excbt.datafuse.nmk.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;
import ru.excbt.datafuse.nmk.data.model.markers.ExCodeObject;
import ru.excbt.datafuse.nmk.data.model.markers.ExSystemObject;

@Entity
@Table(name = "cont_zpoint")
public class ContZPoint extends AbstractAuditableModel implements ExSystemObject, ExCodeObject, DeletableObjectId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@OneToOne()
	@JoinColumn(name = "cont_object_id", insertable = false, updatable = false)
	@JsonIgnore
	private ContObject contObject;

	@Column(name = "cont_object_id")
	private Long contObjectId;

	@OneToOne
	@JoinColumn(name = "cont_service_type", updatable = false, insertable = false)
	private ContServiceType contServiceType;

	@Column(name = "cont_service_type")
	private String contServiceTypeKeyname;

	@Column(name = "custom_service_name")
	private String customServiceName;

	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "cont_zpoint_device", joinColumns = @JoinColumn(name = "cont_zpoint_id") ,
			inverseJoinColumns = @JoinColumn(name = "device_object_id") )
	private List<DeviceObject> deviceObjects = new ArrayList<>();

	@Version
	private int version;

	@ManyToOne()
	@JoinColumn(name = "rso_organization_id")
	private Organization rso;

	@Column(name = "checkout_time")
	private String checkoutTime;

	@Column(name = "checkout_day")
	private Integer checkoutDay;

	@Column(name = "is_double_pipe")
	private Boolean doublePipe;

	@Column(name = "is_manual_loading")
	private Boolean isManualLoading;

	@Column(name = "ex_system")
	@JsonIgnore
	private String exSystemKeyname;

	@Column(name = "ex_code")
	@JsonIgnore
	private String exCode;

	@Column(name = "ts_number")
	private Integer tsNumber;

	@Column(name = "deleted")
	private int deleted;

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

	public List<DeviceObject> getDeviceObjects() {
		return deviceObjects;
	}

	public void setDeviceObjects(List<DeviceObject> deviceObjects) {
		this.deviceObjects = deviceObjects;
	}

	public ContServiceType getContServiceType() {
		return contServiceType;
	}

	public void setContServiceType(ContServiceType contServiceType) {
		this.contServiceType = contServiceType;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getCheckoutTime() {
		return checkoutTime;
	}

	public void setCheckoutTime(String checkoutTime) {
		this.checkoutTime = checkoutTime;
	}

	public Integer getCheckoutDay() {
		return checkoutDay;
	}

	public void setCheckoutDay(Integer checkoutDay) {
		this.checkoutDay = checkoutDay;
	}

	public Boolean getDoublePipe() {
		return doublePipe;
	}

	public void setDoublePipe(Boolean doublePipe) {
		this.doublePipe = doublePipe;
	}

	public Organization getRso() {
		return rso;
	}

	public void setRso(Organization rso) {
		this.rso = rso;
	}

	public Boolean getIsManualLoading() {
		return isManualLoading;
	}

	public void setIsManualLoading(Boolean isManualLoading) {
		this.isManualLoading = isManualLoading;
	}

	public Long getContObjectId() {
		return contObjectId;
	}

	public void setContObjectId(Long contObjectId) {
		this.contObjectId = contObjectId;
	}

	public String getContServiceTypeKeyname() {
		return contServiceTypeKeyname;
	}

	public void setContServiceTypeKeyname(String contServiceTypeKeyname) {
		this.contServiceTypeKeyname = contServiceTypeKeyname;
	}

	@Override
	public String getExSystemKeyname() {
		return exSystemKeyname;
	}

	public void setExSystemKeyname(String exSystemKeyname) {
		this.exSystemKeyname = exSystemKeyname;
	}

	@Override
	public String getExCode() {
		return exCode;
	}

	public Integer getTsNumber() {
		return tsNumber;
	}

	public void setTsNumber(Integer tsNumber) {
		this.tsNumber = tsNumber;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

}
