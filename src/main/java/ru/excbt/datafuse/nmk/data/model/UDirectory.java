package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="u_directory")
@EntityListeners({AuditingEntityListener.class})
public class UDirectory extends AbstractAuditableEntity<AuditUser,Long>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4134532910102114066L;

	@Column(name = "directory_name")
	private String directoryName;

	@Column(name = "directory_description")
	private String directoryDescription;	
	
	@OneToOne //(fetch = FetchType.LAZY)
	@JoinColumn(name = "directory_node_id")
	@JsonIgnore
	private UDirectoryNode directoryNode;

	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinTable(name="subscr_role_directory",
    joinColumns=@JoinColumn(name="directory_id"),
    inverseJoinColumns=@JoinColumn(name="subscr_role_id"))
	@JsonIgnore
	private SubscrRole subscrRole;
	
	@Version
	private int version;
	
	public String getDirectoryName() {
		return directoryName;
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}

	public String getDirectoryDescription() {
		return directoryDescription;
	}

	public void setDirectoryDescription(String directoryDescription) {
		this.directoryDescription = directoryDescription;
	}

	public UDirectoryNode getDirectoryNode() {
		return directoryNode;
	}

	public void setDirectoryNode(UDirectoryNode directoryNode) {
		this.directoryNode = directoryNode;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public SubscrRole getSubscrRole() {
		return subscrRole;
	}

	public void setSubscrRole(SubscrRole subscrRole) {
		this.subscrRole = subscrRole;
	}
	
}
