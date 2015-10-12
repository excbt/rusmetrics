package ru.excbt.datafuse.nmk.data.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;

@Entity
@Table(name = "subscriber")
public class Subscriber extends AbstractAuditableModel {

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

	// @OneToMany(fetch = FetchType.LAZY)
	// @JoinTable(name = "subscr_cont_object", joinColumns = @JoinColumn(name =
	// "subscriber_id") ,
	// inverseJoinColumns = @JoinColumn(name = "cont_object_id") )
	// @JsonIgnore
	// private Collection<ContObject> contObjects;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "subscriber")
	@JsonIgnore
	private List<SubscrContObject> subscrContObjects = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "subscr_directory", joinColumns = @JoinColumn(name = "subscriber_id") ,
			inverseJoinColumns = @JoinColumn(name = "directory_id") )
	@JsonIgnore
	private Collection<UDirectory> directories;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "subscr_rso", joinColumns = @JoinColumn(name = "subscriber_id") ,
			inverseJoinColumns = @JoinColumn(name = "organization_id") )
	@JsonIgnore
	private Collection<Organization> rsoOrganizations;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "timezone_def")
	private TimezoneDef timezoneDef;

	@Column(name = "subscriber_uuid")
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

	// public Collection<ContObject> getContObjects() {
	// return contObjects;
	// }
	//
	// public void setContObjects(final Collection<ContObject> contObjects) {
	// this.contObjects = contObjects;
	// }

	public Collection<UDirectory> getDirectories() {
		return directories;
	}

	public void setDirectories(Collection<UDirectory> directories) {
		this.directories = directories;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Collection<Organization> getRsoOrganizations() {
		return rsoOrganizations;
	}

	public void setRsoOrganizations(Collection<Organization> rsoOrganizations) {
		this.rsoOrganizations = rsoOrganizations;
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

	public List<SubscrContObject> getSubscrContObjects() {
		return subscrContObjects;
	}

	public void setSubscrContObjects(List<SubscrContObject> subscrContObjects) {
		this.subscrContObjects = subscrContObjects;
	}

}
