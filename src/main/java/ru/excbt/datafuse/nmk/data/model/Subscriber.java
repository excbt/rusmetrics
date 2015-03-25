package ru.excbt.datafuse.nmk.data.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="subscriber")
@EntityListeners({AuditingEntityListener.class})
public class Subscriber extends AbstractAuditableEntity<AuditUser, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Column (name="subscriber_name")
	private String name;

	@Column (name="subscriber_info")
	private String info;

	@Column (name="subscriber_comment")
	private String comment;
	
	@ManyToOne 
	@JoinColumn(name = "organization_id")
    private Organization organization;

	
	@OneToMany (fetch = FetchType.EAGER)
    @JoinTable(name="subscr_cont",
    joinColumns=@JoinColumn(name="subscriber_id"),
    inverseJoinColumns=@JoinColumn(name="cont_object_id"))
	private Collection<ContObject> contObjects;
	
	
	@OneToMany (fetch = FetchType.LAZY)
    @JoinTable(name="subscr_directory",
    joinColumns=@JoinColumn(name="subscriber_id"),
    inverseJoinColumns=@JoinColumn(name="directory_id"))
	@JsonIgnore
	private Collection<UDirectory> directories;	

	
	@Version
	private int version;
	
	public String getName() {
		return name;
	}

	public void setName(String role) {
		this.name = role;
	}

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

	public Collection<ContObject> getContObjects() {
		return contObjects;
	}

	public void setContObjects(final Collection<ContObject> contObjects) {
		this.contObjects = contObjects;
	}

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

	
	
}
