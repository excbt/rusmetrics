package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.LocalPlace;
import ru.excbt.datafuse.nmk.data.model.Organization;

@Getter
@Setter
public class TemperatureChartDTO {

    private Long id;

    private Long localPlaceId;

    private Long rsoOrganizationId;

    private String chartName;

    private String chartDescaription;

    private String chartComment;

    private Double chartDeviationValue;

    private Boolean isDefault;

    private Double maxT;

    private Double minT;

    private Boolean flagAdjPump;

    private Boolean flagElevator;

    private Boolean isOk;

    private int version;

    private Organization.OrganizationInfo rsoOrganizationInfo;

    private LocalPlace.LocalPlaceInfo localPlaceInfo;

}
