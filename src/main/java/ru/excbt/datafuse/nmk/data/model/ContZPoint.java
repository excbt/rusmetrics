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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;
import ru.excbt.datafuse.nmk.data.model.markers.ExCodeObject;
import ru.excbt.datafuse.nmk.data.model.markers.ExSystemObject;

/**
 * Подписка контейнера на ресурсные системы
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 26.02.2015
 *
 */
@Entity
@Table(name = "cont_zpoint")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class ContZPoint extends AbstractAuditableModel implements ExSystemObject, ExCodeObject, DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@OneToOne()
	@JoinColumn(name = "cont_object_id")
	@JsonIgnore
	private ContObject contObject;

	@Column(name = "cont_object_id", insertable = false, updatable = false)
	private Long contObjectId;

	@OneToOne
	@JoinColumn(name = "cont_service_type")
	private ContServiceType contServiceType;

	@Column(name = "cont_service_type", updatable = false, insertable = false)
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
	@JoinTable(name = "cont_zpoint_device", joinColumns = @JoinColumn(name = "cont_zpoint_id"),
			inverseJoinColumns = @JoinColumn(name = "device_object_id"))
	@Fetch(value = FetchMode.SUBSELECT)
	private List<DeviceObject> deviceObjects = new ArrayList<>();

	@Version
	private int version;

	@ManyToOne()
	@JoinColumn(name = "rso_organization_id")
	private Organization rso;

	@Column(name = "rso_organization_id", updatable = false, insertable = false)
	private Long rsoId;

	@Column(name = "checkout_time")
	private String checkoutTime;

	@Column(name = "checkout_day")
	private Integer checkoutDay;

	@Column(name = "is_double_pipe")
	private Boolean doublePipe;

	@Column(name = "is_manual_loading")
	private Boolean isManualLoading;

	@Column(name = "ex_system")
	private String exSystemKeyname;

	@Column(name = "ex_code")
	private String exCode;

	@Column(name = "ts_number")
	private Integer tsNumber;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	@Column(name = "is_manual")
	private Boolean isManual;

	@Column(name = "cont_zpoint_comment")
	private String contZPointComment;

	@Column(name = "is_drools_disable")
	private Boolean isDroolsDisable;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "temperature_chart_id")
	private TemperatureChart temperatureChart;

	@Column(name = "temperature_chart_id", insertable = false, updatable = false)
	private Long temperatureChartId;

	@Transient
	private Long _activeDeviceObjectId;

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

	public Boolean getIsManual() {
		return isManual;
	}

	public void setIsManual(Boolean isManual) {
		this.isManual = isManual;
	}

	public void setExCode(String exCode) {
		this.exCode = exCode;
	}

	@JsonIgnore
	public DeviceObject get_activeDeviceObject() {
		return deviceObjects != null && deviceObjects.size() > 0 ? deviceObjects.get(0) : null;
	}

	public Long getRsoId() {
		return rsoId;
	}

	public void setRsoId(Long rsoId) {
		this.rsoId = rsoId;
	}

	public String getContZPointComment() {
		return contZPointComment;
	}

	public void setContZPointComment(String contZPointComment) {
		this.contZPointComment = contZPointComment;
	}

	public Long get_activeDeviceObjectId() {
		if (_activeDeviceObjectId == null) {
			DeviceObject d = get_activeDeviceObject();
			_activeDeviceObjectId = d != null ? d.getId() : null;
		}
		return _activeDeviceObjectId;
	}

	public void set_activeDeviceObjectId(Long _activeDeviceObjectId) {
		this._activeDeviceObjectId = _activeDeviceObjectId;
	}

	public Boolean getIsDroolsDisable() {
		return isDroolsDisable;
	}

	public void setIsDroolsDisable(Boolean isDroolsDisable) {
		this.isDroolsDisable = isDroolsDisable;
	}

	public TemperatureChart getTemperatureChart() {
		return temperatureChart;
	}

	public void setTemperatureChart(TemperatureChart temperatureChart) {
		this.temperatureChart = temperatureChart;
	}

	public Long getTemperatureChartId() {
		return temperatureChartId;
	}

	public void setTemperatureChartId(Long temperatureChartId) {
		this.temperatureChartId = temperatureChartId;
	}

}
