/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_service_pack_special")
public class SubscrServicePackSpecial extends JsonAbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5882132038839681219L;

	@Column(name = "subscr_service_pack_id")
	private Long subscrServicePackId;

	@Column(name = "subscriber_id")
	private Long subscriberId;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public Long getSubscrServicePackId() {
		return subscrServicePackId;
	}

	public void setSubscrServicePackId(Long subscrServicePackId) {
		this.subscrServicePackId = subscrServicePackId;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
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