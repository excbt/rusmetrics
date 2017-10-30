package ru.excbt.datafuse.nmk.data.ptree;

import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;

public class PTreeElement extends PTreeNode {

    public PTreeElement() {
        super(PTreeNodeType.ELEMENT);
    }

    public PTreeContObjectNode addContObjectNode(ContObjectDTO contObjectDTO) {
        PTreeContObjectNode contObjectNode = new PTreeContObjectNode(contObjectDTO);
        this.childNodes.add(contObjectNode);
        return contObjectNode;
    }

    public PTreeElement addChildElement(PTreeElement child) {
        this.childNodes.add(child);
        return child;
    }

    public PTreeElement nodeName(String val) {
        this.setNodeName(val);
        return this;
    }


}
