package ru.excbt.datafuse.nmk.data.ptree;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.Links;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@JsonPropertyOrder({"_id", "nodeType", "nodeName", "childNodes", "linkedNodeObjects"})
public abstract class PTreeNode {

    private PTreeNode parent;

    private final PTreeNodeType nodeType;

    private Long _id;

    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    protected final List<PTreeNode> childNodes;

    private final List<PTreeNodeObject<?>> linkedNodeObjects;

    private final Links links;

    private String nodeName;

    public PTreeNode(PTreeNodeType nodeType) {
        this.nodeType = nodeType;
        this.childNodes = new ArrayList<>();
        this.linkedNodeObjects = new LinkedList<>();
        this.links = new Links();
    }

    public PTreeNode getParent() {
        return parent;
    }

    public void setParent(PTreeNode parent) {
        this.parent = parent;
    }

    public PTreeNodeType getNodeType() {
        return nodeType;
    }

    public Links getLinks() {
        return links;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public List<PTreeNode> getChildNodes() {
        return childNodes;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public List<PTreeNodeObject<?>> getLinkedNodeObjects() {
        return linkedNodeObjects;
    }

    public <T> void addLinkedObject(PTreeNodeObject<T> nodeObject) {
        linkedNodeObjects.add(nodeObject);
    }

}
