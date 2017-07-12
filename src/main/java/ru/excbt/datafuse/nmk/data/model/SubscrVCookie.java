package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.hibernate.types.StringJsonUserType;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_vcookie")
@TypeDefs({ @TypeDef(name = "StringJsonObject", typeClass = StringJsonUserType.class) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//@JsonIgnoreProperties(value = { PropertyFilter.DEV_COMMENT_PROPERTY_IGNORE, "deleted", "id" }, ignoreUnknown = true)
@Getter
@Setter
public class SubscrVCookie extends JsonAbstractAuditableModel implements DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = -7674107555412412995L;

	@JsonIgnore
	@Column(name = "subscriber_id", updatable = false)
	private Long subscriberId;

	@JsonIgnore
	@Column(name = "subscr_user_id", updatable = false)
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

}
