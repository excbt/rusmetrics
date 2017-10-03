package ru.excbt.datafuse.nmk.data.ptree;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"nodeType", "nodeName", "nodeObject", "childNodes"})
public class PTreeNodeObject<T> extends PTreeNode {

    protected final T nodeObject;

    public PTreeNodeObject(PTreeNodeType nodeType, T nodeObject) {
        super(nodeType);
        this.nodeObject = nodeObject;
    }

    public T getNodeObject() {
        return nodeObject;
    }
}
