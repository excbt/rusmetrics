package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "email_notification_type")
public class EmailNotificationType extends JsonAbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3783889810049924035L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "notification_type_name")
	private String notificationTypeName;

	@Column(name = "notification_type_description")
	private String notificationTypeDescription;

	@Column(name = "notification_type_comment")
	private String notificationTypeComment;

	@Column(name = "dev_comment")
	private String devComment;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getNotificationTypeName() {
		return notificationTypeName;
	}

	public void setNotificationTypeName(String notificationTypeName) {
		this.notificationTypeName = notificationTypeName;
	}

	public String getNotificationTypeDescription() {
		return notificationTypeDescription;
	}

	public void setNotificationTypeDescription(String notificationTypeDescription) {
		this.notificationTypeDescription = notificationTypeDescription;
	}

	public String getNotificationTypeComment() {
		return notificationTypeComment;
	}

	public void setNotificationTypeComment(String notificationTypeComment) {
		this.notificationTypeComment = notificationTypeComment;
	}

	public String getDevComment() {
		return devComment;
	}

	public void setDevComment(String devComment) {
		this.devComment = devComment;
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