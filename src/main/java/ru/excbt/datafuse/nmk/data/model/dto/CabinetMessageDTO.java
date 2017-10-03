package ru.excbt.datafuse.nmk.data.model.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the CabinetMessage entity.
 */
public class CabinetMessageDTO implements Serializable {

    private Long id;

    //@NotNull
    private String messageType;

    //@NotNull
    private String messageDirection;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long fromPortalSubscriberId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long fromPortalUserId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long toPortalSubscriberId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long toPortalUserId;

    private String messageBody;

    private Long masterId;

    private Long responseToId;

    private ZonedDateTime creationDateTime;

    private ZonedDateTime reviewDateTime;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CabinetMessageDTO cabinetMessageDTO = (CabinetMessageDTO) o;
        if(cabinetMessageDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), cabinetMessageDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CabinetMessageDTO{" +
            "id=" + getId() +
            ", messageType='" + getMessageType() + "'" +
            ", messageDirection='" + getMessageDirection() + "'" +
            ", fromSubscriberId='" + getFromPortalSubscriberId() + "'" +
            ", fromPortalUserId='" + getFromPortalUserId() + "'" +
            ", toPortalSubscriberId='" + getToPortalSubscriberId() + "'" +
            ", toPortalUserId='" + getToPortalUserId() + "'" +
            ", messageBody='" + getMessageBody() + "'" +
            ", masterId='" + getMasterId() + "'" +
            ", responseToId='" + getResponseToId() + "'" +
            ", creationDateTime='" + getCreationDateTime() + "'" +
            ", reviewDateTime='" + getReviewDateTime() + "'" +
            "}";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageDirection() {
        return messageDirection;
    }

    public void setMessageDirection(String messageDirection) {
        this.messageDirection = messageDirection;
    }

    public Long getFromPortalSubscriberId() {
        return fromPortalSubscriberId;
    }

    public void setFromPortalSubscriberId(Long fromPortalSubscriberId) {
        this.fromPortalSubscriberId = fromPortalSubscriberId;
    }

    public Long getFromPortalUserId() {
        return fromPortalUserId;
    }

    public void setFromPortalUserId(Long fromPortalUserId) {
        this.fromPortalUserId = fromPortalUserId;
    }

    public Long getToPortalSubscriberId() {
        return toPortalSubscriberId;
    }

    public void setToPortalSubscriberId(Long toPortalSubscriberId) {
        this.toPortalSubscriberId = toPortalSubscriberId;
    }

    public Long getToPortalUserId() {
        return toPortalUserId;
    }

    public void setToPortalUserId(Long toPortalUserId) {
        this.toPortalUserId = toPortalUserId;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public Long getResponseToId() {
        return responseToId;
    }

    public void setResponseToId(Long responseToId) {
        this.responseToId = responseToId;
    }

    public ZonedDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(ZonedDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public ZonedDateTime getReviewDateTime() {
        return reviewDateTime;
    }

    public void setReviewDateTime(ZonedDateTime reviewDateTime) {
        this.reviewDateTime = reviewDateTime;
    }
}
