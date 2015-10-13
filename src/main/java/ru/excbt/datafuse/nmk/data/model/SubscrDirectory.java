package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(name = "subscr_directory")
public class SubscrDirectory extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4169289603719698288L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	private Subscriber subscriber;

	@Column(name = "subscriber_id", insertable = false, updatable = false)
	private Long subscriberId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "directory_id")
	private UDirectory udirectory;

	@Column(name = "directory_id", insertable = false, updatable = false)
	private Long udirectoryId;

	@Version
	@Column(name = "version")
	private int version;

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

	public UDirectory getUdirectory() {
		return udirectory;
	}

	public void setUdirectory(UDirectory udirectory) {
		this.udirectory = udirectory;
	}

	public Long getUdirectoryId() {
		return udirectoryId;
	}

	public void setUdirectoryId(Long udirectoryId) {
		this.udirectoryId = udirectoryId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
