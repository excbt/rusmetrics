package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;

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
	
	@OneToOne
	@JoinColumn(name = "directory_node_id")
	private UDirectoryNode directoryNode;

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
	
}
