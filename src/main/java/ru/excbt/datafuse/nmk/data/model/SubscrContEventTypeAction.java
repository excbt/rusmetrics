package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

/**
 * Действия для типа уведомлений
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.12.2015
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "subscr_cont_event_type_action")
@Getter
@Setter
public class SubscrContEventTypeAction extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = -2322682172623184659L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id", updatable = false)
	@JsonIgnore
	private Subscriber subscriber;

	@Column(name = "subscriber_id", updatable = false, insertable = false)
	private Long subscriberId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_event_type_id")
	@JsonIgnore
	private ContEventType contEventType;

	@Column(name = "cont_event_type_id", updatable = false, insertable = false)
	private Long contEventTypeId;

	@Column(name = "subscr_action_user_id")
	private Long subscrActionUserId;

	@Column(name = "is_email")
	private Boolean isEmail;

	@Column(name = "is_sms")
	private Boolean isSms;

	@Version
	@JsonIgnore
	private int version;

	@Column(name = "deleted")
	@JsonIgnore
	private int deleted;

}
