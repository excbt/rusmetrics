package ru.excbt.datafuse.nmk.service.vm;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SubscriberCustomerVM {

    private Long id;

    private String subscriberName;

    private String info;

    private String comment;

    private Long organizationId;

    private String timezoneDefKeyname;

    private int version;

    private String contactEmail;

}
