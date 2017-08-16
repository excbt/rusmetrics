package ru.excbt.datafuse.nmk.data.ptree;

import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;

public class PTreeContZPointNode extends PTreeNodeObject<ContZPointDTO>  {

    public PTreeContZPointNode(ContZPointDTO contZPoint) {
            super(PTreeNodeType.CONT_ZPOINT, contZPoint);
    }

    public PTreeDeviceNode addDeviceObject(DeviceObjectDTO deviceObject) {
        PTreeDeviceNode deviceNode = new PTreeDeviceNode(deviceObject);
        this.childNodes.add(deviceNode);
        return deviceNode;
    }

}
