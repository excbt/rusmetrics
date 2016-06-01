package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_SLOG, name = "log_session_step")
public class LogSessionStep extends JsonAbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7317990456243598785L;

	@Column(name = "session_id")
	private Long sessionId;

	@Column(name = "step_uuid")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID stepUuid;

	@Column(name = "step_date")
	private Date stepDate;

	@Column(name = "step_module")
	private String stepModule;

	@Column(name = "step_type")
	private String stepType;

	@Column(name = "step_message")
	private String stepMessage;

	@Column(name = "step_description")
	private String stepDescription;

	@Column(name = "is_checked")
	private Boolean isChecked;

	@Column(name = "sum_rows")
	private Integer sumRows;

	@Column(name = "is_incremental")
	private Boolean isIncremental;

	@Column(name = "last_increment_date")
	private Date lastIncrementDate;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}