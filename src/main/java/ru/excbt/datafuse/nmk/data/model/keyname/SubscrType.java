package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_type")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class SubscrType extends JsonAbstractKeynameEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -2818498827737120028L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "subscr_type_name")
	private String subscrTypeName;

	@Column(name = "subscr_type_description")
	private String subscrTypeDescription;

	@Column(name = "subscr_type_comment")
	private String subscrTypeComment;

	@Column(name = "subscr_type_order")
	private Integer subscrTypeOrder;

	@Column(name = "is_rma")
	private Boolean isRma;

	@Column(name = "is_child")
	private Boolean isChild;

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

	public String getSubscrTypeName() {
		return subscrTypeName;
	}

	public void setSubscrTypeName(String subscrTypeName) {
		this.subscrTypeName = subscrTypeName;
	}

	public String getSubscrTypeDescription() {
		return subscrTypeDescription;
	}

	public void setSubscrTypeDescription(String subscrTypeDescription) {
		this.subscrTypeDescription = subscrTypeDescription;
	}

	public String getSubscrTypeComment() {
		return subscrTypeComment;
	}

	public void setSubscrTypeComment(String subscrTypeComment) {
		this.subscrTypeComment = subscrTypeComment;
	}

	public Integer getSubscrTypeOrder() {
		return subscrTypeOrder;
	}

	public void setSubscrTypeOrder(Integer subscrTypeOrder) {
		this.subscrTypeOrder = subscrTypeOrder;
	}

	public Boolean getIsRma() {
		return isRma;
	}

	public void setIsRma(Boolean isRma) {
		this.isRma = isRma;
	}

	public Boolean getIsChild() {
		return isChild;
	}

	public void setIsChild(Boolean isChild) {
		this.isChild = isChild;
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
