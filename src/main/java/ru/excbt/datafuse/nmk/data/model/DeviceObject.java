package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;
import ru.excbt.datafuse.nmk.data.model.markers.ExSystemObject;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectShortInfo;
import ru.excbt.datafuse.nmk.data.model.support.DataSourceInfo;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;

/**
 * Прибор
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 26.02.2015
 *
 */
@Entity
@Table(name = "device_object")
public class DeviceObject extends JsonAbstractAuditableModel implements ExSystemObject, DeletableObjectId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -199459403017867220L;

	@JsonIgnoreProperties(ignoreUnknown = true, allowSetters = false)
	public class ContObjectInfo implements Serializable, ContObjectShortInfo {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1872748374864397365L;

		public ContObjectInfo() {

		}

		@Override
		public String getFullName() {
			return contObject == null ? null : contObject.getFullName();
		}

		@Override
		public String getName() {
			return contObject == null ? null : contObject.getName();
		}

		@Override
		public Long getContObjectId() {
			return contObject == null ? null : contObject.getId();
		}
	}

	/**
	 * 
	 * 
	 * @author A.Kovtonyuk
	 * @version 1.0
	 * @since dd.02.2016
	 *
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class DeviceLoginInfo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3989821130568000219L;

		private String deviceLogin;

		private String devicePassword;

		/**
		 * 
		 */
		public DeviceLoginInfo() {

		}

		/**
		 * 
		 * @param deviceObject
		 */
		public DeviceLoginInfo(DeviceObject deviceObject) {
			this.deviceLogin = deviceObject.deviceLogin;
			this.devicePassword = deviceObject.devicePassword;
		}

		/**
		 * 
		 * @return
		 */
		public String getDeviceLogin() {
			return deviceLogin;
		}

		/**
		 * 
		 * @param deviceLogin
		 */
		public void setDeviceLogin(String deviceLogin) {
			this.deviceLogin = deviceLogin;
		}

		/**
		 * 
		 * @return
		 */
		public String getDevicePassword() {
			return devicePassword;
		}

		/**
		 * 
		 * @param devicePassword
		 */
		public void setDevicePassword(String devicePassword) {
			this.devicePassword = devicePassword;
		}
	}

	@Transient
	private final ContObjectInfo contObjectInfo = new ContObjectInfo();

	@Transient
	private DataSourceInfo editDataSourceInfo;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "device_model_id", nullable = false)
	private DeviceModel deviceModel;

	@Column(name = "device_model_id", insertable = false, updatable = false)
	private Long deviceModelId;

	@Column(name = "device_object_number")
	private String number;

	@Column(name = "ex_code", updatable = false)
	private String exCode;

	@Column(name = "ex_label", updatable = false)
	private String exLabel;

	@Column(name = "ex_system", updatable = false)
	private String exSystemKeyname;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "cont_device_object", //
			joinColumns = @JoinColumn(name = "device_object_id"), //
			inverseJoinColumns = @JoinColumn(name = "cont_object_id"))
	@JsonIgnore
	private ContObject contObject;

	@ManyToMany(mappedBy = "deviceObject", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<DeviceObjectDataSource> deviceObjectDataSources = new ArrayList<>();

	@Version
	private int version;
	@JsonIgnore

	@Column(name = "deleted")
	private int deleted;

	@Column(name = "is_device_object_metadata")
	private Boolean isDeviceObjectMetadata;

	@Column(name = "is_manual")
	private Boolean isManual;

	@Column(name = "verification_interval")
	private BigDecimal verificationInterval;

	@Column(name = "verification_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date verificationDate;

	@Column(name = "meta_version")
	private Integer metaVersion = 1;

	@JsonIgnore
	@Column(name = "device_login")
	private String deviceLogin;

	@JsonIgnore
	@Column(name = "device_password")
	private String devicePassword;

	@Column(name = "is_hex_password")
	private Boolean isHexPassword;

	@Column(name = "is_time_sync_enabled")
	private Boolean isTimeSyncEnabled;

	@Transient
	private DeviceLoginInfo deviceLoginInfo;

	@OneToOne(mappedBy = "deviceObject")
	private DeviceObjectLastInfo deviceObjectLastInfo;

	@Column(name = "is_impulse")
	private Boolean isImpulse;

	@Column(name = "impulse_k")
	private Integer impulseK;

	@Column(name = "impulse_mu")
	private String impulseMu;

	@Column(name = "impulse_counter_addr")
	private String impulseCounterAddr;

	@Column(name = "impulse_counter_slot_addr")
	private String impulseCounterSlotAddr;

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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isMetaVzletExpected() {
		return ExSystemKey.VZLET.isEquals(exSystemKeyname);
	}

	@Override
	public String getExSystemKeyname() {
		return exSystemKeyname;
	}

	public void setExSystemKeyname(String exSystemKeyname) {
		this.exSystemKeyname = exSystemKeyname;
	}

	public String getExLabel() {
		return exLabel;
	}

	public void setExLabel(String exLabel) {
		this.exLabel = exLabel;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public Long getDeviceModelId() {
		return deviceModelId;
	}

	public void setDeviceModelId(Long deviceModelId) {
		this.deviceModelId = deviceModelId;
	}

	public ContObject getContObject() {
		return contObject;
	}

	public void setContObject(ContObject contObject) {
		this.contObject = contObject;
	}

	public List<DeviceObjectDataSource> getDeviceObjectDataSources() {
		return deviceObjectDataSources;
	}

	public void setDeviceObjectDataSources(List<DeviceObjectDataSource> deviceObjectDataSources) {
		this.deviceObjectDataSources = deviceObjectDataSources;
	}

	public ContObjectInfo getContObjectInfo() {
		return contObjectInfo;
	}

	public DeviceObjectDataSource getActiveDataSource() {
		Optional<DeviceObjectDataSource> dataSource = ObjectFilters.activeFilter(deviceObjectDataSources.stream())
				.findFirst();
		DeviceObjectDataSource result = dataSource.isPresent() ? dataSource.get() : null;
		return result;
	}

	/**
	 * 
	 */
	public void loadLazyProps() {
		getActiveDataSource();
		if (getContObjectInfo() != null) {
			getContObjectInfo().getContObjectId();
		}
	}

	public Boolean getIsManual() {
		return isManual;
	}

	public void setIsManual(Boolean isManual) {
		this.isManual = isManual;
	}

	public Boolean getIsDeviceObjectMetadata() {
		return isDeviceObjectMetadata;
	}

	public void setIsDeviceObjectMetadata(Boolean isDeviceObjectMetadata) {
		this.isDeviceObjectMetadata = isDeviceObjectMetadata;
	}

	public DataSourceInfo getEditDataSourceInfo() {
		if (editDataSourceInfo == null) {
			DeviceObjectDataSource dods = getActiveDataSource();
			editDataSourceInfo = dods != null ? new DataSourceInfo(dods) : new DataSourceInfo();
		}
		return editDataSourceInfo;
	}

	public void setEditDataSourceInfo(DataSourceInfo dataSourceInfo) {
		this.editDataSourceInfo = dataSourceInfo;
	}

	public BigDecimal getVerificationInterval() {
		return verificationInterval;
	}

	public void setVerificationInterval(BigDecimal verificationInterval) {
		this.verificationInterval = verificationInterval;
	}

	public Date getVerificationDate() {
		return verificationDate;
	}

	public void setVerificationDate(Date verificationDate) {
		this.verificationDate = verificationDate;
	}

	public Integer getMetaVersion() {
		return metaVersion;
	}

	public void setMetaVersion(Integer metaVersion) {
		this.metaVersion = metaVersion;
	}

	public String getDeviceLogin() {
		return deviceLogin;
	}

	public void setDeviceLogin(String deviceLogin) {
		this.deviceLogin = deviceLogin;
	}

	public String getDevicePassword() {
		return devicePassword;
	}

	public void setDevicePassword(String devicePassword) {
		this.devicePassword = devicePassword;
	}

	public Boolean getIsHexPassword() {
		return isHexPassword;
	}

	public void setIsHexPassword(Boolean isHexPassword) {
		this.isHexPassword = isHexPassword;
	}

	public Boolean getIsTimeSyncEnabled() {
		return isTimeSyncEnabled;
	}

	public void setIsTimeSyncEnabled(Boolean isTimeSyncEnabled) {
		this.isTimeSyncEnabled = isTimeSyncEnabled;
	}

	public DeviceLoginInfo getDeviceLoginInfo() {
		return deviceLoginInfo;
	}

	public void setDeviceLoginInfo(DeviceLoginInfo deviceLoginInfo) {
		this.deviceLoginInfo = deviceLoginInfo;
	}

	/**
	 * 
	 */
	@JsonIgnore
	public void shareDeviceLoginInfo() {
		this.deviceLoginInfo = new DeviceLoginInfo(this);
	}

	/**
	 * 
	 */
	@JsonIgnore
	public void saveDeviceObjectInfo() {
		if (deviceLoginInfo != null
		//&& deviceLoginInfo.deviceLogin != null && deviceLoginInfo.devicePassword != null
		) {
			this.deviceLogin = deviceLoginInfo.deviceLogin != null ? deviceLoginInfo.deviceLogin : "";
			this.devicePassword = deviceLoginInfo.devicePassword != null ? deviceLoginInfo.devicePassword : "";
		}
	}

	@JsonProperty
	public DeviceObjectLastInfo getDeviceObjectLastInfo() {
		return deviceObjectLastInfo;
	}

	@JsonIgnore
	public void setDeviceObjectLastInfo(DeviceObjectLastInfo deviceObjectLastInfo) {
		this.deviceObjectLastInfo = deviceObjectLastInfo;
	}

	public Boolean getIsImpulse() {
		return isImpulse;
	}

	public void setIsImpulse(Boolean isImpulse) {
		this.isImpulse = isImpulse;
	}

	public Integer getImpulseK() {
		return impulseK;
	}

	public void setImpulseK(Integer impulseK) {
		this.impulseK = impulseK;
	}

	public String getImpulseMu() {
		return impulseMu;
	}

	public void setImpulseMu(String impulseMu) {
		this.impulseMu = impulseMu;
	}

	public String getImpulseCounterAddr() {
		return impulseCounterAddr;
	}

	public void setImpulseCounterAddr(String impulseCounterAddr) {
		this.impulseCounterAddr = impulseCounterAddr;
	}

	public String getImpulseCounterSlotAddr() {
		return impulseCounterSlotAddr;
	}

	public void setImpulseCounterSlotAddr(String impulseCounterSlotAddr) {
		this.impulseCounterSlotAddr = impulseCounterSlotAddr;
	}

}
