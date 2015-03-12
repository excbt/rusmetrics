package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import ru.excbt.datafuse.nmk.data.domain.IdEntity;
import ru.excbt.datafuse.nmk.data.domain.RowAudit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "node_directory")
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeDirectory extends IdEntity implements Serializable {

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
	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
	@JoinColumn(name = "parent_id")
	@OrderColumn(name = "id")
	private Collection<NodeDirectory> childNodes = new ArrayList<>();

	@Column(name = "parent_id")
	private Long parentId;

	@Embedded
	@JsonIgnore
	private RowAudit rowAudit;

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static NodeDirectory newInstance(String name) {
		NodeDirectory result = new NodeDirectory();
		result.setRowAudit(RowAudit.newInstanceNow());
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

	public RowAudit getRowAudit() {
		return rowAudit;
	}

	public void setRowAudit(RowAudit rowAudit) {
		this.rowAudit = rowAudit;
	}

	@Transient
	@JsonIgnore
	public boolean isRoot() {
		return parentId == null;
	}

	public Collection<NodeDirectory> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(Collection<NodeDirectory> childNodes) {
		this.childNodes = childNodes;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

}
