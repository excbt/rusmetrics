package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "email_notification")
@Getter
@Setter
public class EmailNotification extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 5091171385850198906L;

	@Column(name = "email_notification_type")
	private String emailNotificationType;

	@Column(name = "from_subscr_user_id")
	private Long fromSubscrUserId;

	@Column(name = "to_subscr_user_id")
	private Long toSubscrUserId;

	@Column(name = "message_from")
	private String messageFrom;

	@Column(name = "message_to")
	private String messageTo;

	@Column(name = "mail_host")
	private String mailHost;

	@Column(name = "message_subject")
	private String messageSubject;

	@Column(name = "is_text_html")
	private String isTextHtml;

	@Column(name = "message_text")
	private String messageText;

	@Column(name = "message_create_date")
	private Date messageCreateDate = new Date();

	@Column(name = "message_send_date")
	private Date mesageSendDate;

	@Column(name = "is_send_complete")
	private Boolean isSendComplete;

	@Column(name = "is_recieved")
	private Boolean isRecieved;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
