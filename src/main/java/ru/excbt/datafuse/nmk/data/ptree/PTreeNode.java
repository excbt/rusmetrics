package ru.excbt.datafuse.nmk.data.ptree;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Links;

import java.util.ArrayList;
import java.util.List;

public class PTreeNode {

    private PTreeNode parent;

    private final PTreeNodeType nodeType;

    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private final List<PTreeNode> children;

    private final Links links;

    private String nodeName;

    public PTreeNode(PTreeNodeType nodeType) {
        this.nodeType = nodeType;
        this.children = new ArrayList<>();
        this.links = new Links();
    }

    public PTreeContObjectNode addContObject () {
        PTreeContObjectNode contObjectNode = new PTreeContObjectNode();
        this.children.add(contObjectNode);
        return contObjectNode;
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

    public List<PTreeNode> getChildren() {
        return children;
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
}
