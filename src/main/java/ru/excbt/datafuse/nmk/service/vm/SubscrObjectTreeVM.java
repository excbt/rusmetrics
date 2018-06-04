package ru.excbt.datafuse.nmk.service.vm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscrObjectTreeVM {

    private Long id;

    private Long parentId;

    private Long rmaSubscriberId;

    private Long subscriberId;

    private Boolean isRma;

    private String objectTreeType;

    private String objectName;

    private String objectDescription;

    private String objectComment;

    private Long templateId;

    private Long templateItemId;

    private Boolean isLinkDeny;

    private int version;

    private String treeMode;

}
