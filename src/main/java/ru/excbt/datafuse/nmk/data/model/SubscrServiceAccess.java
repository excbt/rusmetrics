package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Доступ к услугам абонента
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.09.2015
 *
 */
@Entity
@Table(name = "subscr_service_access")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
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
	@Temporal(TemporalType.DATE)
	private Date accessStartDate;

	@Column(name = "access_end_date")
	@Temporal(TemporalType.DATE)
	private Date accessEndDate;

	@Version
	@Column(name = "version")
	private int version;

	public static SubscrServiceAccess newInstance() {
		SubscrServiceAccess result = new SubscrServiceAccess();
		return result;
	};

	public static SubscrServiceAccess newInstance(SubscrServiceAccess srcPackItem) {
		SubscrServiceAccess result = new SubscrServiceAccess();
		result.packId = srcPackItem.packId;
		result.itemId = srcPackItem.itemId;
		return result;
	}

	public static SubscrServiceAccess newInstance(Long packId, Long itemId) {
		SubscrServiceAccess result = new SubscrServiceAccess();
		result.packId = packId;
		result.itemId = itemId;
		return result;
	}

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

	/**
	 * 
	 * @param other
	 * @return
	 */
	public boolean equalsPackItem(SubscrServiceAccess other) {
		if (other == null) {
			return false;
		}

		if (this.getPackId() == null && other.getPackId() == null && this.getItemId() == null
				&& other.getItemId() == null) {
			return true;
		}

		if (this.getPackId() != null && other.getPackId() != null && this.getItemId() == null
				&& other.getItemId() == null) {
			return this.getPackId().equals(other.getPackId());
		}

		if (this.getPackId() != null && other.getPackId() != null && this.getItemId() != null
				&& other.getItemId() != null) {
			return this.getPackId().equals(other.getPackId()) && this.getItemId().equals(other.getItemId());
		}

		return false;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
