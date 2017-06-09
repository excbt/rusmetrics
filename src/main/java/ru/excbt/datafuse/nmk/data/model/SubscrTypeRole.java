package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_type_role")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class SubscrTypeRole extends JsonAbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 4889186302635036698L;

	@Column(name = "subscr_type")
	private String subscrType;

	@Column(name = "subscr_role_name")
	private String subscrRoleName;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public String getSubscrType() {
		return subscrType;
	}

	public void setSubscrType(String subscrType) {
		this.subscrType = subscrType;
	}

	public String getSubscrRoleName() {
		return subscrRoleName;
	}

	public void setSubscrRoleName(String subscrRoleName) {
		this.subscrRoleName = subscrRoleName;
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
