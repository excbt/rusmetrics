package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * РСО для абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.10.2015
 *
 */
@Entity
@Table(name = "subscr_rso")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class SubscrRso extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 722462130319702458L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	private Subscriber subscriber;

	@Column(name = "subscriber_id", insertable = false, updatable = false)
	private Long subscriberId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@Column(name = "organization_id", insertable = false, updatable = false)
	private Long organizationId;

	@Version
	@Column(name = "version")
	private int version;

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

}
