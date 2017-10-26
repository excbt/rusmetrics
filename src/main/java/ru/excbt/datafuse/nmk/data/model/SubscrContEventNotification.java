package ru.excbt.datafuse.nmk.data.model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.support.ContEventTypeModel;

/**
 * Уведомления абонента по событиям
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.06.2015
 *
 */
@Entity
@Table(name = "subscr_cont_event_notification")
@JsonIgnoreProperties(ignoreUnknown = true)
@DynamicUpdate
@Getter
@Setter
public class SubscrContEventNotification extends AbstractAuditableModel implements ContEventTypeModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 691445476392471888L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id", updatable = false)
	@JsonIgnore
	private Subscriber subscriber;

	@Column(name = "subscriber_id", insertable = false, updatable = false)
	private Long subscriberId;

	@Transient
	private ContEvent contEvent;

	@Column(name = "cont_event_id", insertable = false, updatable = false)
	private Long contEventId;

	@Column(name = "cont_event_level", insertable = false, updatable = false)
	private Integer contEventLevel;

	@Column(name = "cont_event_level_color", insertable = false, updatable = false)
	private String contEventLevelColor;

	@Column(name = "cont_event_time", insertable = false, updatable = false)
	private LocalDateTime contEventTime;

	@Column(name = "cont_object_id", insertable = false, updatable = false)
	private Long contObjectId;

	@Column(name = "cont_event_type_id", insertable = false, updatable = false)
	private Long contEventTypeId;

	@Transient
	private ContEventType contEventType;

	@Column(name = "is_new")
	private Boolean isNew;

	@Column(name = "notification_time", insertable = false, updatable = false)
	private LocalDateTime notificationTime;

	@Column(name = "notification_time_tz", insertable = false, updatable = false)
	private ZonedDateTime notificationTimeTZ;

	@Column(name = "revision_time")
	private LocalDateTime revisionTime;

	@Column(name = "revision_time_tz")
	private ZonedDateTime revisionTimeTZ;

	@Column(name = "revision_subscr_user_id")
	private Long revisionSubscrUserId;

	@Column(name = "cont_event_category", insertable = false, updatable = false)
	private String contEventCategoryKeyname;

	@Column(name = "cont_event_deviation", insertable = false, updatable = false)
	private String contEventDeviationKeyname;

	@Column(name = "mon_version")
	@NotNull
	private Short monVersion = 1;

}
