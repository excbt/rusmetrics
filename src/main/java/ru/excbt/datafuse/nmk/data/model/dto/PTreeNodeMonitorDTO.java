package ru.excbt.datafuse.nmk.data.model.dto;

import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.ptree.PTreeNodeType;

import java.io.Serializable;

public class PTreeNodeMonitorDTO implements Serializable {

    private final PTreeNodeType nodeType;

    private final Long monitorObjectId;

    private ContEventLevelColorKeyV2 colorKey;

    public PTreeNodeMonitorDTO(PTreeNodeType nodeType, Long monitorObjectId) {
        this.nodeType = nodeType;
        this.monitorObjectId = monitorObjectId;
    }

    public PTreeNodeMonitorDTO(PTreeNodeType nodeType, Long monitorObjectId, ContEventLevelColorKeyV2 colorKey) {
        this.nodeType = nodeType;
        this.monitorObjectId = monitorObjectId;
        this.colorKey = colorKey;
    }

    public PTreeNodeType getNodeType() {
        return nodeType;
    }

    public ContEventLevelColorKeyV2 getColorKey() {
        return colorKey;
    }

    public void setColorKey(ContEventLevelColorKeyV2 colorKey) {
        this.colorKey = colorKey;
    }

    public Long getMonitorObjectId() {
        return monitorObjectId;
    }
}
