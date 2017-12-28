package ru.excbt.datafuse.nmk.data.ptree;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"nodeType", "nodeName", "nodeObject", "childNodes"})
public class PTreeNodeObject<T> extends PTreeNode {

    public enum DataType {FULL, STUB}

    protected final T nodeObject;

    protected final DataType dataType;

    public PTreeNodeObject(PTreeNodeType nodeType, T nodeObject) {
        super(nodeType);
        this.nodeObject = nodeObject;
        this.dataType = DataType.FULL;
    }

    public PTreeNodeObject(PTreeNodeType nodeType, T nodeObject, DataType dataType) {
        super(nodeType);
        this.nodeObject = nodeObject;
        this.dataType = dataType;
    }

    public T getNodeObject() {
        return nodeObject;
    }

    public DataType getDataType() {
        return dataType;
    }
}
