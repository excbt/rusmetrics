package ru.excbt.datafuse.nmk.service.vm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberVM {

    private Long id;

    private Long organizationId;

    private String subscriberName;

    private String subscriberInfo;

    private String subscriberComment;

    private String timezoneDef;

    private String subscrType;

    private String contactEmail;

    private String rmaLdapOu;

    private boolean canCreateChild;

    private int version;

    private String organizationInn;

    private String organizationName;

}
