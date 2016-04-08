package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_object_tree_template")
public class SubscrObjectTreeTemplate extends JsonAbstractAuditableModel implements DeletableObjectId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5479029319239649305L;

	@Column(name = "object_tree_type")
	private String objectTreeType;

	@Column(name = "rma_subscriber_id")
	private Long rmaSubscriberId;

	//	@Column(name = "subscriber_id")
	//	private Long subscriberId;

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

	public String getObjectTreeType() {
		return objectTreeType;
	}

	public void setObjectTreeType(String objectTreeType) {
		this.objectTreeType = objectTreeType;
	}

	public Long getRmaSubscriberId() {
		return rmaSubscriberId;
	}

	public void setRmaSubscriberId(Long rmaSubscriberId) {
		this.rmaSubscriberId = rmaSubscriberId;
	}

	//	public Long getSubscriberId() {
	//		return subscriberId;
	//	}
	//
	//	public void setSubscriberId(Long subscriberId) {
	//		this.subscriberId = subscriberId;
	//	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateCaption() {
		return templateCaption;
	}

	public void setTemplateCaption(String templateCaption) {
		this.templateCaption = templateCaption;
	}

	public String getTemplateDescription() {
		return templateDescription;
	}

	public void setTemplateDescription(String templateDescription) {
		this.templateDescription = templateDescription;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public Boolean getIsCommon() {
		return isCommon;
	}

	public void setIsCommon(Boolean isCommon) {
		this.isCommon = isCommon;
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

	public Integer getTemplateOrder() {
		return templateOrder;
	}

	public void setTemplateOrder(Integer templateOrder) {
		this.templateOrder = templateOrder;
	}

}