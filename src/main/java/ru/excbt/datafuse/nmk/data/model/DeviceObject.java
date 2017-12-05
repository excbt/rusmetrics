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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.domain.PersistableBuilder;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.dto.ActiveDataSourceInfoDTO;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;
import ru.excbt.datafuse.nmk.data.model.markers.ExSystemObject;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectShortInfo;
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
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class DeviceObject extends JsonAbstractAuditableModel implements ExSystemObject, DeletableObjectId, PersistableBuilder<DeviceObject, Long> {

    /**
	 *
	 */
	private static final long serialVersionUID = -199459403017867220L;

	private static final Logger log = LoggerFactory.getLogger(DeviceObject.class);

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
    @NoArgsConstructor
    @ToString
    @Getter
    @Setter
	public static class DeviceLoginInfo implements Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = -3989821130568000219L;

		private String deviceLogin;

		private String devicePassword;


		/**
		 *
		 * @param deviceObject
		 */
		public DeviceLoginInfo(DeviceObject deviceObject) {
			this.deviceLogin = deviceObject.deviceLogin;
			this.devicePassword = deviceObject.devicePassword;
		}

	}

	@Transient
    @Getter
	private final ContObjectInfo contObjectInfo = new ContObjectInfo();

	@Transient
	private ActiveDataSourceInfoDTO editDataSourceInfo = new ActiveDataSourceInfoDTO();

	@OneToOne (fetch = FetchType.EAGER)
	@JoinColumn(name = "device_model_id", nullable = false)
    //@Fetch(value = FetchMode.SUBSELECT)
    @Getter
    @Setter
	private DeviceModel deviceModel;

	@Column(name = "device_model_id", insertable = false, updatable = false)
    @Getter
    @Setter
	private Long deviceModelId;

	@Column(name = "device_object_number")
    @Getter
    @Setter
	private String number;

	@Column(name = "ex_code", updatable = false)
    @Getter
    @Setter
	private String exCode;

	@Column(name = "ex_label", updatable = false)
    @Getter
    @Setter
	private String exLabel;

	@Column(name = "ex_system", updatable = false)
    @Getter
    @Setter
	private String exSystemKeyname;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "cont_device_object", //
			joinColumns = @JoinColumn(name = "device_object_id"), //
			inverseJoinColumns = @JoinColumn(name = "cont_object_id"))
	@JsonIgnore
    @Getter
    @Setter
	private ContObject contObject;

	@ManyToMany(mappedBy = "deviceObject", fetch = FetchType.LAZY)
	@JsonIgnore
    @Getter
    @Setter
	private List<DeviceObjectDataSource> deviceObjectDataSources = new ArrayList<>();

	@Version
    @Getter
    @Setter
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
    @Getter
    @Setter
	private int deleted;

    @Getter
    @Setter
	@Column(name = "is_device_object_metadata")
	private Boolean isDeviceObjectMetadata;

    @Getter
    @Setter
	@Column(name = "is_manual")
	private Boolean isManual;

    @Getter
    @Setter
	@Column(name = "verification_interval", columnDefinition = "numeric")
	private Double verificationInterval;

    @Getter
    @Setter
	@Column(name = "verification_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date verificationDate;

    @Getter
    @Setter
	@Column(name = "meta_version")
	private Integer metaVersion = 1;

    @JsonIgnore
    @Getter
    @Setter
	@Column(name = "device_login")
	private String deviceLogin;

    @JsonIgnore
    @Getter
    @Setter
	@Column(name = "device_password")
	private String devicePassword;

    @Getter
    @Setter
	@Column(name = "is_hex_password")
	private Boolean isHexPassword;

    @Getter
    @Setter
    @Column(name = "is_time_sync_enabled")
    private Boolean isTimeSyncEnabled;

    @Getter
    @Setter
	@Transient
	private DeviceLoginInfo deviceLoginInfo;


    @Getter
    @Setter
	@Column(name = "is_impulse")
	private Boolean isImpulse;

    @Getter
    @Setter
	@Column(name = "impulse_k", columnDefinition = "numeric")
	private Double impulseK;

    @Getter
    @Setter
	@Column(name = "impulse_mu")
	private String impulseMu;

    @Getter
    @Setter
	@Column(name = "impulse_counter_addr")
	private String impulseCounterAddr;

	@Getter
    @Setter
	@Column(name = "impulse_counter_slot_addr")
	private String impulseCounterSlotAddr;

    @Getter
    @Setter
    @Column(name = "impulse_counter_type")
	private String impulseCounterType;

    @Size(min = 1, max = 1)
    @Pattern(regexp = "^[P|S]{1}$", message ="Must be P or S")
    @Column(name = "inst_type")
    @Getter
    @Setter
    private String instType;

    @ManyToOne
    @JoinColumn(name = "heat_radiator_type_id", nullable = true)
    @Getter
    @Setter
    private HeatRadiatorType heatRadiatorType;

    @Column(name = "heat_radiator_power", nullable = false, columnDefinition = "numeric(12,2)", precision = 12, scale = 2 )
    @Getter
    @Setter
    private Double heatRadiatorPower;

    @Column(name = "heat_radiator_type_id", insertable = false, updatable = false)
    @Getter
    @Setter
    private Long heatRadiatorTypeId;

    @Column(name = "device_object_name")
    @Getter
    @Setter
    private String deviceObjectName;

    @OneToOne(mappedBy = "deviceObject")
    private V_DeviceObjectTimeOffset timeOffset;

    @OneToOne(mappedBy = "deviceObject")
    private DeviceObjectLastInfo deviceObjectLastInfo;

	public boolean isMetaVzletExpected() {
		return ExSystemKey.VZLET.isEquals(exSystemKeyname);
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

	public ActiveDataSourceInfoDTO getEditDataSourceInfo() {
		if (editDataSourceInfo == null || editDataSourceInfo.getSubscrDataSourceId() == null) {
			DeviceObjectDataSource activeDS = getActiveDataSource();
			editDataSourceInfo = activeDS != null ? new ActiveDataSourceInfoDTO(activeDS) : new ActiveDataSourceInfoDTO();
		}
		return editDataSourceInfo;
	}

	public void setEditDataSourceInfo(ActiveDataSourceInfoDTO dataSourceInfo) {
		this.editDataSourceInfo = dataSourceInfo;
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
	public void saveDeviceObjectCredentials() {
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

    public V_DeviceObjectTimeOffset getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(V_DeviceObjectTimeOffset timeOffset) {
        this.timeOffset = timeOffset;
    }

    @Override
    public String toString() {
        return "DeviceObject{" +
            "id=" + getId() +
            ", number='" + number + '\'' +
            ", exCode='" + exCode + '\'' +
            ", exLabel='" + exLabel + '\'' +
            ", exSystemKeyname='" + exSystemKeyname + '\'' +
            ", version=" + version +
            ", deleted=" + deleted +
            ", isDeviceObjectMetadata=" + isDeviceObjectMetadata +
            ", isManual=" + isManual +
            ", verificationInterval=" + verificationInterval +
            ", verificationDate=" + verificationDate +
            ", metaVersion=" + metaVersion +
            ", deviceLogin='" + deviceLogin + '\'' +
            ", devicePassword='" + devicePassword + '\'' +
            ", isHexPassword=" + isHexPassword +
            ", isTimeSyncEnabled=" + isTimeSyncEnabled +
            ", deviceLoginInfo=" + deviceLoginInfo +
            ", isImpulse=" + isImpulse +
            ", impulseK=" + impulseK +
            ", impulseMu='" + impulseMu + '\'' +
            ", impulseCounterAddr='" + impulseCounterAddr + '\'' +
            ", impulseCounterSlotAddr='" + impulseCounterSlotAddr + '\'' +
            ", impulseCounterType='" + impulseCounterType + '\'' +
            ", activeDataSourceInfo=" + getActiveDataSource() +
            '}';
    }
}
