package ru.excbt.datafuse.nmk.service.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.security.AuthoritiesConstants;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public SubscrUserDTO(SubscrUser subscrUser) {
        this.id = subscrUser.getId();
        this.userName = subscrUser.getUserName();
        this.userNickname = subscrUser.getUserNickname();
        this.version = subscrUser.getVersion();
        this.subscriberId = subscrUser.getSubscriber() != null ? subscrUser.getSubscriber().getId() : null;
        this.userUuid = subscrUser.getUserUUID();
        this.userComment = subscrUser.getUserComment();
        this.userEMail = subscrUser.getUserEMail();
        this.isBlocked = subscrUser.getIsBlocked();
        this.contactEmail = subscrUser.getContactEmail();
        this.userDescription = subscrUser.getUserDescription();
        this.isAdmin = Boolean.TRUE.equals(subscrUser.getIsAdmin());
        this.isReadonly = Boolean.TRUE.equals(subscrUser.getIsReadonly());
        this.authorities = subscrUser.getSubscrRoles().stream().map(SubscrRole::getRoleName).collect(Collectors.toSet());
        if (Boolean.TRUE.equals(subscrUser.getIsAdmin())) {
            this.authorities.add(AuthoritiesConstants.ADMIN);
        }
    }


}
