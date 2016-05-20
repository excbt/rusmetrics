package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_pref")
public class SubscrPref extends JsonAbstractKeynameEntity implements DisabledObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 322159788896491335L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "comment")
	private String comment;

	@Column(name = "subscr_pref_category")
	private String subscrPrefCategory;

	@Column(name = "pref_name")
	private String prefName;

	@Column(name = "pref_description")
	private String prefDescription;

	@Column(name = "pref_comment")
	private String prefComment;

	@Column(name = "pref_value_type")
	private String prefValueType;

	@Column(name = "pref_order")
	private Integer prefOrder;

	@Column(name = "dev_comment")
	private String devComment;

	@Column(name = "is_active_caption")
	private String isActiveCaption;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	@JsonIgnore
	@Column(name = "is_disabled")
	private Boolean isDisabled;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPrefName() {
		return prefName;
	}

	public void setPrefName(String prefName) {
		this.prefName = prefName;
	}

	public String getPrefDescription() {
		return prefDescription;
	}

	public void setPrefDescription(String prefDescription) {
		this.prefDescription = prefDescription;
	}

	public String getPrefComment() {
		return prefComment;
	}

	public void setPrefComment(String prefComment) {
		this.prefComment = prefComment;
	}

	public String getPrefValueType() {
		return prefValueType;
	}

	public void setPrefValueType(String prefValueType) {
		this.prefValueType = prefValueType;
	}

	public Integer getPrefOrder() {
		return prefOrder;
	}

	public void setPrefOrder(Integer prefOrder) {
		this.prefOrder = prefOrder;
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

	@Override
	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public String getSubscrPrefCategory() {
		return subscrPrefCategory;
	}

	public void setSubscrPrefCategory(String subscrPrefCategory) {
		this.subscrPrefCategory = subscrPrefCategory;
	}

	public String getIsActiveCaption() {
		return isActiveCaption;
	}

	public void setIsActiveCaption(String isActiveCaption) {
		this.isActiveCaption = isActiveCaption;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

}