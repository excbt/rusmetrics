package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(name = "subscr_service_access")
public class SubscrServiceAccess extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6016201239685238614L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	@JsonIgnore
	private Subscriber subscriber;

	@Column(name = "subscriber_id", insertable = false, updatable = false)
	private Long subscriberId;

	@Column(name = "subscr_service_pack_id")
	private Long packId;

	@Column(name = "subscr_service_item_id")
	private Long itemId;

	@Column(name = "access_start_date")
	private Date accessStartDate;

	@Column(name = "access_end_date")
	private Date accessEndDate;

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public Long getPackId() {
		return packId;
	}

	public void setPackId(Long packId) {
		this.packId = packId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Date getAccessStartDate() {
		return accessStartDate;
	}

	public void setAccessStartDate(Date accessStartDate) {
		this.accessStartDate = accessStartDate;
	}

	public Date getAccessEndDate() {
		return accessEndDate;
	}

	public void setAccessEndDate(Date accessEndDate) {
		this.accessEndDate = accessEndDate;
	}

}
