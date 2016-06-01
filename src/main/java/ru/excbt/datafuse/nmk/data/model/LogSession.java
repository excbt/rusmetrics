package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_SLOG, name = "log_session")
public class LogSession extends JsonAbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4849894945576991987L;

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

	@Column(name = "session_owner")
	private String sessionOwner;

	@Column(name = "session_message")
	private String sessionMessage;

	@Column(name = "device_object_id")
	private Long deviceObjectId;

	@Column(name = "session_status")
	private String sessionStatus;

	@Column(name = "status_message")
	private String statusMessage;

	@Column(name = "status_date")
	private Date statusDate;

	@Column(name = "status_nanos")
	private Long statusNanos;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

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

	public Long getDeviceObjectId() {
		return deviceObjectId;
	}

	public void setDeviceObjectId(Long deviceObjectId) {
		this.deviceObjectId = deviceObjectId;
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

}