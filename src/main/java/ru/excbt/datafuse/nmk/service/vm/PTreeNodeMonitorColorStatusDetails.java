package ru.excbt.datafuse.nmk.service.vm;

import lombok.Getter;

import java.util.List;

@Getter
public class PTreeNodeMonitorColorStatusDetails {
    private List<Long> contObjectIds;

    public PTreeNodeMonitorColorStatusDetails(List<Long> contObjectIds) {
        this.contObjectIds = contObjectIds;
    }
}
