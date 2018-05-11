package ru.excbt.datafuse.nmk.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class SubscrUserDTO {

    private Long id;

    private String userName;

    private String userNickname;

    private int version;

    private Long subscriberId;

    private UUID userUuid;

    private String userComment;

    private String userEMail;

    private Boolean isBlocked;

    private String contactEmail;

    private String userDescription;

    private boolean isAdmin;

    private boolean isReadonly;

    private Set<String> authorities;

}
