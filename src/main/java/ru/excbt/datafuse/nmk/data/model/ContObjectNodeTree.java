package ru.excbt.datafuse.nmk.data.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_object_node_tree")
public class ContObjectNodeTree extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4600296369525597730L;

	@Column(name = "node_tree_type")
	private String nodeTreeType;

	@Column(name = "parent_id", insertable = false, updatable = false)
	private Long parentId;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "parent_id")
	private ContObjectNodeTree parent;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "parent", cascade = CascadeType.ALL)
	private List<ContObjectNodeTree> childNodeList = new ArrayList<>();

	@Column(name = "cont_object_id")
	private Long contObjectId;

	@Column(name = "node_name")
	private String nodeName;

	@Column(name = "node_description")
	private String nodeDescription;

	@Column(name = "node_comment")
	private String nodeComment;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public String getNodeTreeType() {
		return nodeTreeType;
	}

	public void setNodeTreeType(String nodeTreeType) {
		this.nodeTreeType = nodeTreeType;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public ContObjectNodeTree getParent() {
		return parent;
	}

	public void setParent(ContObjectNodeTree parent) {
		this.parent = parent;
	}

	public Long getContObjectId() {
		return contObjectId;
	}

	public void setContObjectId(Long contObjectId) {
		this.contObjectId = contObjectId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public List<ContObjectNodeTree> getChildNodeList() {
		return childNodeList;
	}

	public void setChildNodeList(List<ContObjectNodeTree> childNodeList) {
		this.childNodeList = childNodeList;
	}

}