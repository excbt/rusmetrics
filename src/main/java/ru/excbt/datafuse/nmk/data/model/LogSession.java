package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_SLOG, name = "log_session")
public class LogSession extends JsonAbstractAuditableModel {

	/**
		 * 
		 */
	private static final long serialVersionUID = 574203986772513167L;

	@Column(name = "master_id")
	private Long masterId;

	@Column(name = "session_type")
	private String sessionType;

	@Column(name = "session_uuid")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID sessionUuid;

	@Column(name = "master_session_uuid")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID masterSessionUuid;

	@Column(name = "session_date")
	private Date sessionDate;

	@Column(name = "session_end_date")
	private Date sessionEndDate;

	@Column(name = "session_owner")
	private String sessionOwner;

	@Column(name = "session_message")
	private String sessionMessage;

	@Column(name = "session_status")
	private String sessionStatus;

	@Column(name = "status_message")
	private String statusMessage;

	@Column(name = "status_date")
	private Date statusDate;

	@Column(name = "status_nanos")
	private Long statusNanos;

	@Column(name = "data_source_id")
	private Long dataSourceId;

	@Column(name = "device_object_id")
	private Long deviceObjectId;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "is_complete")
	private Boolean isComplete;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	@JsonIgnore
	@Transient
	private SubscrDataSource subscrDataSource;

	@JsonIgnore
	@Transient
	private DeviceObject deviceObject;

	public Long getMasterId() {
		return masterId;
	}

	public void setMasterId(Long masterId) {
		this.masterId = masterId;
	}

	public String getSessionType() {
		return sessionType;
	}

	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}

	public UUID getSessionUuid() {
		return sessionUuid;
	}

	public void setSessionUuid(UUID sessionUuid) {
		this.sessionUuid = sessionUuid;
	}

	public UUID getMasterSessionUuid() {
		return masterSessionUuid;
	}

	public void setMasterSessionUuid(UUID masterSessionUuid) {
		this.masterSessionUuid = masterSessionUuid;
	}

	public Date getSessionDate() {
		return sessionDate;
	}

	public void setSessionDate(Date sessionDate) {
		this.sessionDate = sessionDate;
	}

	public Date getSessionEndDate() {
		return sessionEndDate;
	}

	public void setSessionEndDate(Date sessionEndDate) {
		this.sessionEndDate = sessionEndDate;
	}

	public String getSessionOwner() {
		return sessionOwner;
	}

	public void setSessionOwner(String sessionOwner) {
		this.sessionOwner = sessionOwner;
	}

	public String getSessionMessage() {
		return sessionMessage;
	}

	public void setSessionMessage(String sessionMessage) {
		this.sessionMessage = sessionMessage;
	}

	public String getSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(String sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Long getStatusNanos() {
		return statusNanos;
	}

	public void setStatusNanos(Long statusNanos) {
		this.statusNanos = statusNanos;
	}

	public Long getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(Long dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public Long getDeviceObjectId() {
		return deviceObjectId;
	}

	public void setDeviceObjectId(Long deviceObjectId) {
		this.deviceObjectId = deviceObjectId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsComplete() {
		return isComplete;
	}

	public void setIsComplete(Boolean isComplete) {
		this.isComplete = isComplete;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public SubscrDataSource getSubscrDataSource() {
		return subscrDataSource;
	}

	public void setSubscrDataSource(SubscrDataSource subscrDataSource) {
		this.subscrDataSource = subscrDataSource;
	}

	public DeviceObject getDeviceObject() {
		return deviceObject;
	}

	public void setDeviceObject(DeviceObject deviceObject) {
		this.deviceObject = deviceObject;
	}

}