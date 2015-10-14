package ru.excbt.datafuse.nmk.data.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

@Entity
@Table(name = "subscriber")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Subscriber extends AbstractAuditableModel implements DeletableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "subscriber_name")
	private String subscriberName;

	@Column(name = "subscriber_info")
	private String info;

	@Column(name = "subscriber_comment")
	private String comment;

	@ManyToOne
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@Column(name = "organization_id", updatable = false, insertable = false)
	private Long organizationId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "timezone_def")
	private TimezoneDef timezoneDef;

	@Column(name = "subscriber_uuid", insertable = false)
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID subscriberUUID;

	@Version
	private int version;

	@Column(name = "is_rma")
	private Boolean isRma;

	@Column(name = "rma_subscriber_id")
	private Long rmaSubscriberId;

	@Column(name = "ghost_subscriber_id")
	private Long ghostSubscriberId;

	@Column(name = "deleted")
	private int deleted;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public String getSubscriberName() {
		return subscriberName;
	}

	public void setSubscriberName(String subscriberName) {
		this.subscriberName = subscriberName;
	}

	public TimezoneDef getTimezoneDef() {
		return timezoneDef;
	}

	public void setTimezoneDef(TimezoneDef timezoneDef) {
		this.timezoneDef = timezoneDef;
	}

	public UUID getSubscriberUUID() {
		return subscriberUUID;
	}

	public void setSubscriberUUID(UUID subscriberUUID) {
		this.subscriberUUID = subscriberUUID;
	}

	public Boolean getIsRma() {
		return isRma;
	}

	public void setIsRma(Boolean isRma) {
		this.isRma = isRma;
	}

	public Long getRmaSubscriberId() {
		return rmaSubscriberId;
	}

	public void setRmaSubscriberId(Long rmaSubscriberId) {
		this.rmaSubscriberId = rmaSubscriberId;
	}

	public Long getGhostSubscriberId() {
		return ghostSubscriberId;
	}

	public void setGhostSubscriberId(Long ghostSubscriberId) {
		this.ghostSubscriberId = ghostSubscriberId;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

}
