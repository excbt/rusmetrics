package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointShortInfo;

import javax.persistence.Version;
import java.util.Date;

/**
 * Created by kovtonyk on 07.07.2017.
 */
@Getter
@Setter
public class ContZPointDTO {


    @Getter
    @Builder
    @AllArgsConstructor
    public static class ShortInfo implements ContZPointShortInfo {
        private final Long contZPointId;
        private final Long contObjectId;
        private final String customServiceName;
        private final String contServiceType;
        private final String contServiceTypeCaption;


        public ShortInfo (ContZPoint contZPoint) {
            contZPointId = contZPoint.getId();
            contObjectId = contZPoint.getContObjectId();
            customServiceName = contZPoint.getCustomServiceName();
            contServiceType = contZPoint.getContServiceType().getKeyname();
            contServiceTypeCaption = contZPoint.getContServiceType().getCaption();
        }

    }

    private Long id;
    private Long contObjectId;
    private String contServiceTypeKeyname;
    private String customServiceName;
    private Date startDate;
    private Date endDate;

    private String contZPointComment;

    private int version;

}
