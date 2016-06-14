package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;
import ru.excbt.datafuse.nmk.utils.DateFormatUtils;

@Entity
@Table(schema = DBMetadata.SCHEME_SLOG, name = "log_session_step")
@SequenceGenerator(name = "abstractEntity", sequenceName = "slog.seq_log_session_id", allocationSize = 1)
public class LogSessionStep extends JsonAbstractAuditableModel implements DeletableObjectId {

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

	@JsonIgnore
	@Column(name = "step_order")
	private Integer stepOrder;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public UUID getStepUuid() {
		return stepUuid;
	}

	public void setStepUuid(UUID stepUuid) {
		this.stepUuid = stepUuid;
	}

	public Date getStepDate() {
		return stepDate;
	}

	public void setStepDate(Date stepDate) {
		this.stepDate = stepDate;
	}

	public String getStepModule() {
		return stepModule;
	}

	public void setStepModule(String stepModule) {
		this.stepModule = stepModule;
	}

	public String getStepType() {
		return stepType;
	}

	public void setStepType(String stepType) {
		this.stepType = stepType;
	}

	public String getStepMessage() {
		return stepMessage;
	}

	public void setStepMessage(String stepMessage) {
		this.stepMessage = stepMessage;
	}

	public String getStepDescription() {
		return stepDescription;
	}

	public void setStepDescription(String stepDescription) {
		this.stepDescription = stepDescription;
	}

	public Boolean getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(Boolean isChecked) {
		this.isChecked = isChecked;
	}

	public Integer getSumRows() {
		return sumRows;
	}

	public void setSumRows(Integer sumRows) {
		this.sumRows = sumRows;
	}

	public Boolean getIsIncremental() {
		return isIncremental;
	}

	public void setIsIncremental(Boolean isIncremental) {
		this.isIncremental = isIncremental;
	}

	public Date getLastIncrementDate() {
		return lastIncrementDate;
	}

	public void setLastIncrementDate(Date lastIncrementDate) {
		this.lastIncrementDate = lastIncrementDate;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public Integer getStepOrder() {
		return stepOrder;
	}

	public void setStepOrder(Integer stepOrder) {
		this.stepOrder = stepOrder;
	}

	public String getStepDateStr() {
		return stepDate == null ? null
				: DateFormatUtils.formatDateTime(stepDate, DateFormatUtils.DATE_FORMAT_STR_FULL_SEC);
	}

}