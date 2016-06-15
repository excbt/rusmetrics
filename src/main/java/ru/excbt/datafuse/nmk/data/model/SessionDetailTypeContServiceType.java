package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.SessionDetailType;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "session_detail_type_cont_service_type")
public class SessionDetailTypeContServiceType extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3061671752355415024L;

	@Column(name = "cont_service_type", insertable = false, updatable = false)
	private String contServiceType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_detail_type", insertable = false, updatable = false)
	private SessionDetailType sessionDetailType;

	@Column(name = "order_idx", insertable = false, updatable = false)
	private Integer orderIdx;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public String getContServiceType() {
		return contServiceType;
	}

	public void setContServiceType(String contServiceType) {
		this.contServiceType = contServiceType;
	}

	public Integer getOrderIdx() {
		return orderIdx;
	}

	public void setOrderIdx(Integer orderIdx) {
		this.orderIdx = orderIdx;
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

	public SessionDetailType getSessionDetailType() {
		return sessionDetailType;
	}

	public void setSessionDetailType(SessionDetailType sessionDetailType) {
		this.sessionDetailType = sessionDetailType;
	}

}
