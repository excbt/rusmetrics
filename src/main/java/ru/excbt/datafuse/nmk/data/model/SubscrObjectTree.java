package ru.excbt.datafuse.nmk.data.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_object_tree")
public class SubscrObjectTree extends JsonAbstractAuditableModel implements DeletableObjectId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7126312877491219773L;

	@Column(name = "parent_id", insertable = false, updatable = false)
	private Long parentId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent_id")
	private SubscrObjectTree parent;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "parent", cascade = CascadeType.ALL)
	private List<SubscrObjectTree> childObjectList = new ArrayList<SubscrObjectTree>();

	@Column(name = "rma_subscriber_id")
	private Long rmaSubscriberId;

	@JsonIgnore
	@Column(name = "subscriber_id")
	private Long subscriberId;

	@Column(name = "is_rma")
	private Boolean isRma;

	@Column(name = "object_tree_type")
	private String objectTreeType;

	@Column(name = "object_name")
	private String objectName;

	@Column(name = "object_description")
	private String objectDescription;

	@Column(name = "object_comemnt")
	private String objectComemnt;

	@Column(name = "dev_comment")
	private String devComment;

	@Column(name = "template_id")
	private Long templateId;

	@Column(name = "template_item_id")
	private Long templateItemId;

	@Column(name = "is_link_deny")
	private Boolean isLinkDeny;

	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public SubscrObjectTree getParent() {
		return parent;
	}

	public void setParent(SubscrObjectTree parent) {
		this.parent = parent;
	}

	public Long getRmaSubscriberId() {
		return rmaSubscriberId;
	}

	public void setRmaSubscriberId(Long rmaSubscriberId) {
		this.rmaSubscriberId = rmaSubscriberId;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public Boolean getIsRma() {
		return isRma;
	}

	public void setIsRma(Boolean isRma) {
		this.isRma = isRma;
	}

	public String getObjectTreeType() {
		return objectTreeType;
	}

	public void setObjectTreeType(String objectTreeType) {
		this.objectTreeType = objectTreeType;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectDescription() {
		return objectDescription;
	}

	public void setObjectDescription(String objectDescription) {
		this.objectDescription = objectDescription;
	}

	public String getObjectComemnt() {
		return objectComemnt;
	}

	public void setObjectComemnt(String objectComemnt) {
		this.objectComemnt = objectComemnt;
	}

	public String getDevComment() {
		return devComment;
	}

	public void setDevComment(String devComment) {
		this.devComment = devComment;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Long getTemplateItemId() {
		return templateItemId;
	}

	public void setTemplateItemId(Long templateItemId) {
		this.templateItemId = templateItemId;
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

	public List<SubscrObjectTree> getChildObjectList() {
		return childObjectList;
	}

	public void setChildObjectList(List<SubscrObjectTree> childObjectList) {
		this.childObjectList = childObjectList;
	}

	public Boolean getIsLinkDeny() {
		return isLinkDeny;
	}

	public void setIsLinkDeny(Boolean isLinkDeny) {
		this.isLinkDeny = isLinkDeny;
	}

}