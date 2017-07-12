package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_session_task_log")
@Getter
@Setter
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

}
