package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_object_tree_cont_object")
public class SubscrObjectTreeContObject extends JsonAbstractAuditableModel implements DeletableObjectId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8235602462750786719L;

	@Column(name = "subscr_object_tree_id")
	private Long subscrObjectTreeId;

	@Column(name = "cont_object_id")
	private Long contObjectId;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public Long getSubscrObjectTreeId() {
		return subscrObjectTreeId;
	}

	public void setSubscrObjectTreeId(Long subscrObjectTreeId) {
		this.subscrObjectTreeId = subscrObjectTreeId;
	}

	public Long getContObjectId() {
		return contObjectId;
	}

	public void setContObjectId(Long contObjectId) {
		this.contObjectId = contObjectId;
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

}
