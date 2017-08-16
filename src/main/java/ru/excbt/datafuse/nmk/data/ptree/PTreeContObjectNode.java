package ru.excbt.datafuse.nmk.data.ptree;

import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;

public class PTreeContObjectNode extends PTreeNodeObject<ContObjectDTO> {

    public PTreeContObjectNode(ContObjectDTO contObjectDTO) {
        super(PTreeNodeType.CONT_OBJECT, contObjectDTO);
    }

    public PTreeContZPointNode addContZPoint(ContZPointDTO contZPoint) {
        PTreeContZPointNode contZPointNode = new PTreeContZPointNode(contZPoint);
        this.childNodes.add(contZPointNode);
        return contZPointNode;
    }


}
