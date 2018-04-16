package ru.excbt.datafuse.nmk.service.vm;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PTreeNodeMonitorColorStatus {

    private String levelColor;
    private Integer contObjectCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String contServiceTypeKeyname;

    public PTreeNodeMonitorColorStatus(String levelColor, Integer contObjectCount) {
        this.levelColor = levelColor;
        this.contObjectCount = contObjectCount;
    }

    public PTreeNodeMonitorColorStatus(String levelColor) {
        this.levelColor = levelColor;
        this.contObjectCount = 0;
    }

    public void incCount() {
        this.contObjectCount++;
    }
}
