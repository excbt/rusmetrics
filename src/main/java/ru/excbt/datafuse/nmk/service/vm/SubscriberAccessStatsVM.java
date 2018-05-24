package ru.excbt.datafuse.nmk.service.vm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriberAccessStatsVM {

    private Long id;

    private Long organizationId;

    private String subscriberName;

    private String subscriberInfo;

    private String subscriberComment;

    private String timezoneDefKeyname;

    private String subscrType;

    private String contactEmail;

    private String rmaLdapOu;

    private boolean canCreateChild;

    private int version;

    private String organizationInn;

    private String organizationName;

    private int totalContObjects = 0;

    private int totalContZPoints = 0;
}
