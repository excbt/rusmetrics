package ru.excbt.datafuse.nmk.service.vm;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SubscriberVM {

    private Long id;

    private Long organizationId;

    private String subscriberName;

    private String subscriberInfo;

    private String subscriberComment;

    private String timezoneDef;

    private String subscrType;

    private String contactEmail;

    private int version;

}
