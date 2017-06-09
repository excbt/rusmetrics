package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_object_tree_template")
@Getter
@Setter
public class SubscrObjectTreeTemplate extends JsonAbstractAuditableModel implements DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = 5479029319239649305L;

	@Column(name = "object_tree_type")
	private String objectTreeType;

	@Column(name = "rma_subscriber_id")
	private Long rmaSubscriberId;

	@Column(name = "subscriber_id")
	private Long subscriberId;

	@Column(name = "template_name")
	private String templateName;

	@Column(name = "template_caption")
	private String templateCaption;

	@Column(name = "template_description")
	private String templateDescription;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@Column(name = "is_common")
	private Boolean isCommon;

	@Column(name = "template_order")
	private Integer templateOrder;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
