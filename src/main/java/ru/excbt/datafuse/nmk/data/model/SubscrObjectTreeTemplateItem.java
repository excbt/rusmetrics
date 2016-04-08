package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_object_tree_template_item")
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

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public SubscrObjectTreeTemplate getTemplate() {
		return template;
	}

	public void setTemplate(SubscrObjectTreeTemplate template) {
		this.template = template;
	}

	public Integer getItemLevel() {
		return itemLevel;
	}

	public void setItemLevel(Integer itemLevel) {
		this.itemLevel = itemLevel;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getItemComment() {
		return itemComment;
	}

	public void setItemComment(String itemComment) {
		this.itemComment = itemComment;
	}

	public String getDevComment() {
		return devComment;
	}

	public void setDevComment(String devComment) {
		this.devComment = devComment;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public Boolean getIsSingleObject() {
		return isSingleObject;
	}

	public void setIsSingleObject(Boolean isSingleObject) {
		this.isSingleObject = isSingleObject;
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

	public Boolean getIsLinkDeny() {
		return isLinkDeny;
	}

	public void setIsLinkDeny(Boolean isLinkDeny) {
		this.isLinkDeny = isLinkDeny;
	}

	public String getItemNameTemplate() {
		return itemNameTemplate;
	}

	public void setItemNameTemplate(String itemNameTemplate) {
		this.itemNameTemplate = itemNameTemplate;
	}

}