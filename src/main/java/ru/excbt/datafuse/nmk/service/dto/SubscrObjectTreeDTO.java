package ru.excbt.datafuse.nmk.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SubscrObjectTreeDTO {

    private Long id;

    private Long parentId;

    private List<SubscrObjectTreeDTO> childObjectList = new ArrayList<>();

    private Long rmaSubscriberId;

    private Long subscriberId;

    private Boolean isRma;

    private String objectTreeType;

    private String objectName;

    private String objectDescription;

    private String objectComemnt;

    private Long templateId;

    private Long templateItemId;

    private Boolean isLinkDeny;

    private int version;

}
