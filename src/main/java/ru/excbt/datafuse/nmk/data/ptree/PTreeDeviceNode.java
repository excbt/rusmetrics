package ru.excbt.datafuse.nmk.data.ptree;

import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;

public class PTreeDeviceNode extends PTreeNodeObject<DeviceObjectDTO> {
    public PTreeDeviceNode(DeviceObjectDTO deviceObjectDTO) {
        super(PTreeNodeType.DEVICE_OBJECT, deviceObjectDTO);
    }
}
