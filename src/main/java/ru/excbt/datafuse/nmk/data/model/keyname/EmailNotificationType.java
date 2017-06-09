package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "email_notification_type")
@Getter
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

}
