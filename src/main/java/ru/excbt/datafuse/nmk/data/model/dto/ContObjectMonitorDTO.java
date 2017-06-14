package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 14.06.2017.
 */
@Getter
@Setter
public class ContObjectMonitorDTO extends ContObjectDTO {

    @Getter
    @Setter
    public class ContObjectStats {
        private int contZpointCount;

        private String contEventLevelColor;

        private long eventsCount;

        private long eventsTypesCount;
    }

    private ContObjectStats contObjectStats;

    private long newEventsCount;

}
