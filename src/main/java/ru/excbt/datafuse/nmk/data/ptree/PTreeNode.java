package ru.excbt.datafuse.nmk.data.ptree;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.Links;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"nodeType", "nodeName", "childNodes"})
public abstract class PTreeNode {

    private PTreeNode parent;

    private final PTreeNodeType nodeType;

    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    protected final List<PTreeNode> childNodes;

    private final Links links;

    private String nodeName;

    public PTreeNode(PTreeNodeType nodeType) {
        this.nodeType = nodeType;
        this.childNodes = new ArrayList<>();
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


}
