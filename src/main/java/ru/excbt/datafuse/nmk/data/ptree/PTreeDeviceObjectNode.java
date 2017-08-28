package ru.excbt.datafuse.nmk.data.ptree;

import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;

public class PTreeDeviceObjectNode extends PTreeNodeObject<DeviceObjectDTO> {
    public PTreeDeviceObjectNode(DeviceObjectDTO deviceObjectDTO) {
        super(PTreeNodeType.DEVICE_OBJECT, deviceObjectDTO);
    }
}
