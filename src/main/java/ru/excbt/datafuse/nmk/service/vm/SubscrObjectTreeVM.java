package ru.excbt.datafuse.nmk.service.vm;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.service.dto.SubscrObjectTreeDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SubscrObjectTreeVM {

    private Long id;

    private Long parentId;

    private List<SubscrObjectTreeVM> childObjectList = new ArrayList<>();

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
