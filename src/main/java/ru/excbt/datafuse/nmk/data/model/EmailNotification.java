package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "email_notification")
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

	public String getEmailNotificationType() {
		return emailNotificationType;
	}

	public void setEmailNotificationType(String emailNotificationType) {
		this.emailNotificationType = emailNotificationType;
	}

	public Long getFromSubscrUserId() {
		return fromSubscrUserId;
	}

	public void setFromSubscrUserId(Long fromSubscrUserId) {
		this.fromSubscrUserId = fromSubscrUserId;
	}

	public Long getToSubscrUserId() {
		return toSubscrUserId;
	}

	public void setToSubscrUserId(Long toSubscrUserId) {
		this.toSubscrUserId = toSubscrUserId;
	}

	public String getMessageFrom() {
		return messageFrom;
	}

	public void setMessageFrom(String messageFrom) {
		this.messageFrom = messageFrom;
	}

	public String getMessageTo() {
		return messageTo;
	}

	public void setMessageTo(String messageTo) {
		this.messageTo = messageTo;
	}

	public String getMailHost() {
		return mailHost;
	}

	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	public String getMessageSubject() {
		return messageSubject;
	}

	public void setMessageSubject(String messageSubject) {
		this.messageSubject = messageSubject;
	}

	public String getIsTextHtml() {
		return isTextHtml;
	}

	public void setIsTextHtml(String isTextHtml) {
		this.isTextHtml = isTextHtml;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public Date getMessageCreateDate() {
		return messageCreateDate;
	}

	public void setMessageCreateDate(Date messageCreateDate) {
		this.messageCreateDate = messageCreateDate;
	}

	public Date getMesageSendDate() {
		return mesageSendDate;
	}

	public void setMesageSendDate(Date mesageSendDate) {
		this.mesageSendDate = mesageSendDate;
	}

	public Boolean getIsSendComplete() {
		return isSendComplete;
	}

	public void setIsSendComplete(Boolean isSendComplete) {
		this.isSendComplete = isSendComplete;
	}

	public Boolean getIsRecieved() {
		return isRecieved;
	}

	public void setIsRecieved(Boolean isRecieved) {
		this.isRecieved = isRecieved;
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