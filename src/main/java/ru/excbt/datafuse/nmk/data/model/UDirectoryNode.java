package ru.excbt.datafuse.nmk.data.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "u_directory_node")
@EntityListeners({AuditingEntityListener.class})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UDirectoryNode extends AbstractAuditableEntity<AuditUser,Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "node_name")
	private String nodeName;

	@Column(name = "node_caption")
	private String nodeCaption;

	@Column(name = "node_description")
	private String nodeDescription;

	@Column(name = "node_comment")
	private String nodeComment;

	//@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
	@JoinColumn(name = "parent_id")
	@OrderColumn(name = "id")
	private Collection<UDirectoryNode> childNodes = new ArrayList<>();

	@Column(name = "parent_id")
	private Long parentId;

	@Version
	private int version;
	

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static UDirectoryNode newInstance(String name) {
		UDirectoryNode result = new UDirectoryNode();
		result.setNodeName(name);
		return result;
	}

	
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeCaption() {
		return nodeCaption;
	}

	public void setNodeCaption(String nodeCaption) {
		this.nodeCaption = nodeCaption;
	}

	public String getNodeDescription() {
		return nodeDescription;
	}

	public void setNodeDescription(String nodeDescription) {
		this.nodeDescription = nodeDescription;
	}

	public String getNodeComment() {
		return nodeComment;
	}

	public void setNodeComment(String nodeComment) {
		this.nodeComment = nodeComment;
	}


	@Transient
	@JsonIgnore
	public boolean isRoot() {
		return parentId == null;
	}

	public Collection<UDirectoryNode> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(Collection<UDirectoryNode> childNodes) {
		this.childNodes = childNodes;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}


	public int getVersion() {
		return version;
	}


	public void setVersion(int version) {
		this.version = version;
	}

}
