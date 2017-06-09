package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_object_tree_template_item")
@Getter
@Setter
public class SubscrObjectTreeTemplateItem extends JsonAbstractAuditableModel implements DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = -8142999302180305196L;

	@Column(name = "template_id", insertable = false, updatable = false)
	private Long templateId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_id")
	private SubscrObjectTreeTemplate template;

	@Column(name = "item_level")
	private Integer itemLevel;

	@Column(name = "item_name")
	private String itemName;

	@Column(name = "item_name_template")
	private String itemNameTemplate;

	@Column(name = "item_description")
	private String itemDescription;

	@Column(name = "item_comment")
	private String itemComment;

	@Column(name = "dev_comment")
	private String devComment;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@Column(name = "is_link_deny")
	private Boolean isLinkDeny;

	@Column(name = "is_single_object")
	private Boolean isSingleObject;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
