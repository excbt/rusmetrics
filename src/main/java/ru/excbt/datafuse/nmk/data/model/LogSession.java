package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;
import ru.excbt.datafuse.nmk.utils.DateFormatUtils;

@Entity
@Table(schema = DBMetadata.SCHEME_SLOG, name = "log_session")
@SequenceGenerator(name = "abstractEntity", sequenceName = "slog.seq_log_session_id", allocationSize = 1)
@JsonInclude(value = Include.NON_NULL)
@Getter
@Setter
public class LogSession extends JsonAbstractAuditableModel implements DeletableObjectId {

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

	@Column(name = "author_id")
	private Long authorId;

	@JsonIgnore
	@Transient
	private SubscrDataSource subscrDataSource;

	@JsonIgnore
	@Transient
	private DeviceObject deviceObject;

	@JsonIgnore
	@Transient
	private V_FullUserInfo fullUserInfo;


	public String getSessionDateStr() {
		return sessionDate == null ? null
				: DateFormatUtils.formatDateTime(sessionDate, DateFormatUtils.DATE_FORMAT_STR_FULL_SEC);
	}

	public String getSessionEndDateStr() {
		return sessionEndDate == null ? null
				: DateFormatUtils.formatDateTime(sessionEndDate, DateFormatUtils.DATE_FORMAT_STR_FULL_SEC);
	}

	public String getStatusDateStr() {
		return statusDate == null ? null
				: DateFormatUtils.formatDateTime(statusDate, DateFormatUtils.DATE_FORMAT_STR_FULL_SEC);
	}

}
