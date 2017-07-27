package ru.excbt.datafuse.nmk.data.ptree;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PTreeContObjectNode extends PTreeNode {

    public PTreeContObjectNode() {
        super(PTreeNodeType.CONT_OBJECT);
    }

}
