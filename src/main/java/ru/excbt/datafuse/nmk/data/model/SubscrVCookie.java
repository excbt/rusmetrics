package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.hibernate.types.StringJsonUserType;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_vcookie")
@TypeDefs({ @TypeDef(name = "StringJsonObject", typeClass = StringJsonUserType.class) })
public class SubscrVCookie extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7674107555412412995L;

	@JsonIgnore
	@Column(name = "subscriber_id")
	private Long subscriberId;

	@JsonIgnore
	@Column(name = "subscr_user_id")
	private Long subscrUserId;

	@Column(name = "vc_mode")
	private String vcMode;

	@Column(name = "vc_key")
	private String vcKey;

	@Column(name = "vc_value")
	@Type(type = "StringJsonObject")
	private String vcValue;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	@Column(name = "dev_comment")
	private String devComment;

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getVcMode() {
		return vcMode;
	}

	public void setVcMode(String vcMode) {
		this.vcMode = vcMode;
	}

	public String getVcKey() {
		return vcKey;
	}

	public void setVcKey(String vcKey) {
		this.vcKey = vcKey;
	}

	public String getVcValue() {
		return vcValue;
	}

	public void setVcValue(String vcValue) {
		this.vcValue = vcValue;
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

	public Long getSubscrUserId() {
		return subscrUserId;
	}

	public void setSubscrUserId(Long subscrUserId) {
		this.subscrUserId = subscrUserId;
	}

	public String getDevComment() {
		return devComment;
	}

	public void setDevComment(String devComment) {
		this.devComment = devComment;
	}

}
