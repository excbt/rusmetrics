package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_session_task_log")
public class SubscrSessionTaskLog extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3630088566591405071L;

	@Column(name = "subscr_session_task_id")
	private Long subscrSessionTaskId;

	@Column(name = "log_session_id")
	private Long logSessionId;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public Long getSubscrSessionTaskId() {
		return subscrSessionTaskId;
	}

	public void setSubscrSessionTaskId(Long subscrSessionTaskId) {
		this.subscrSessionTaskId = subscrSessionTaskId;
	}

	public Long getLogSessionId() {
		return logSessionId;
	}

	public void setLogSessionId(Long logSessionId) {
		this.logSessionId = logSessionId;
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