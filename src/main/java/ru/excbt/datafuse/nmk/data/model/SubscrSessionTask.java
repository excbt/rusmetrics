package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_session_task")
@Getter
@Setter
public class SubscrSessionTask extends JsonAbstractAuditableModel implements DeletableObjectId {

	/**
		 *
		 */
	private static final long serialVersionUID = -4509805266922253996L;

	@JsonIgnore
	@Column(name = "subscriber_id", updatable = false)
	private Long subscriberId;

	@JsonIgnore
	@Column(name = "subscr_user_id", updatable = false)
	private Long subscrUserId;

	//	@Column(name = "cont_zpoint_id", updatable = false)
	//	private Long contZPointId;

	@Column(name = "device_object_id", updatable = false)
	private Long deviceObjectId;

	@Column(name = "period_begin_date")
	private Date periodBeginDate;

	@Column(name = "period_end_date")
	private Date periodEndDate;

	@Type(type = "ru.excbt.datafuse.hibernate.types.StringArrayUserType")
	@Column(name = "session_detail_types", updatable = false)
	private String[] sessionDetailTypes;

	@JsonIgnore
	@Column(name = "is_system")
	private Boolean isSystem;

	@Column(name = "task_create_date")
	private Date taskCreateDate;

	@Column(name = "task_start_date")
	private Date taskStartDate;

	@Column(name = "task_end_date")
	private Date taskEndDate;

	@Column(name = "task_is_complete")
	private Boolean taskIsComplete;

	@Column(name = "dev_comment")
	private String devComment;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
