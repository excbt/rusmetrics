package ru.excbt.datafuse.nmk.service.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.dto.SubscriberDTO;
import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;

import java.util.UUID;

@Getter
@Setter
public class V_FullUserInfoDTO {

    private Long id;

    private String userName;

    private String firstName;

    private String lastName;

    private int version;

    private boolean _system;

    private Long subscriberId;

    private SubscriberDTO subscriber;

    private UUID userUUID;

    private Boolean isSystem;

    private Boolean isAdmin;

    private Boolean isReadonly;

    private Boolean canCreateChild;

    private Boolean isChild;

    private String subscrType;

    private Boolean isBlocked;

    public Boolean getIsRma() {
        if (this._system) {
            return true;
        }
        if (this.subscriber == null) {
            return null;
        }
        return subscriber.getIsRma();
    }

    public Boolean getIsCabinet() {
        return SubscrTypeKey.CABINET.getKeyname().equals(subscrType) && Boolean.TRUE.equals(isChild);
    }

}
