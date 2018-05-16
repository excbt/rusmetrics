package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.*;

import java.util.UUID;

/**
 * Created by kovtonyk on 12.07.2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriberDTO {

    private Long id;

    private String subscriberName;

    @Deprecated // use subscriberInfo instead
    private String info;

    private String subscriberInfo;

    @Deprecated // use subscriberComment instead
    private String comment;

    private String subscriberComment;

    private Long organizationId;

    private String timezoneDefKeyname;

    private UUID subscriberUUID;

    private int version;

    private Boolean isRma;

    private Long rmaSubscriberId;

    private Long ghostSubscriberId;

    private Double mapCenterLat;

    private Double mapCenterLng;

    private Double mapZoom;

    private Double mapZoomDetail;

    private Boolean canCreateChild;

    private Boolean isChild;

    private String subscrCabinetNr;

    private String contactEmail;

    private String subscrType;

    private Long parentSubscriberId;

}
