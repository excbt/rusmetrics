package ru.excbt.datafuse.nmk.data.model.dto;

import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.ptree.PTreeNodeType;

import java.io.Serializable;

public class PTreeNodeMonitorDTO implements Serializable {

    private PTreeNodeType pTreeNodeType;

    private Long pTreeNodeId;

    private Long contZPointId;

    private Long contObjectId;

    private ContEventLevelColorKeyV2 colorKey;


    public PTreeNodeType getpTreeNodeType() {
        return pTreeNodeType;
    }

    public void setpTreeNodeType(PTreeNodeType pTreeNodeType) {
        this.pTreeNodeType = pTreeNodeType;
    }

    public Long getpTreeNodeId() {
        return pTreeNodeId;
    }

    public void setpTreeNodeId(Long pTreeNodeId) {
        this.pTreeNodeId = pTreeNodeId;
    }

    public Long getContZPointId() {
        return contZPointId;
    }

    public void setContZPointId(Long contZPointId) {
        this.contZPointId = contZPointId;
    }

    public Long getContObjectId() {
        return contObjectId;
    }

    public void setContObjectId(Long contObjectId) {
        this.contObjectId = contObjectId;
    }

    public ContEventLevelColorKeyV2 getColorKey() {
        return colorKey;
    }

    public void setColorKey(ContEventLevelColorKeyV2 colorKey) {
        this.colorKey = colorKey;
    }
}
