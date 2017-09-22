package ru.excbt.datafuse.nmk.data.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Subselect;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * A CabinetMessage.
 */
@Entity
@Subselect("select * from " +DBMetadata.SCHEME_CABINET2 + ".cabinet_message")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class CabinetMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public static CabinetMessageType DEFAULT_TYPE = CabinetMessageType.REQUEST;
    public static CabinetMessageDirection DEFAULT_DIRECTION = CabinetMessageDirection.OUT;

    @Id
    private Long id;

    @NotNull
    @Column(name = "message_type", nullable = false)
    private String messageType;

    @NotNull
    @Column(name = "message_direction", nullable = false)
    private String messageDirection;

    @Column(name = "from_portal_subscriber_id")
    private Long fromPortalSubscriberId;

    @Column(name = "from_portal_user_id")
    private Long fromPortalUserId;

    @Column(name = "to_portal_subscriber_id")
    private Long toPortalSubscriberId;

    @Column(name = "to_portal_user_id")
    private Long toPortalUserId;

    @Column(name = "message_body")
    private String messageBody;

    @Column(name = "master_id")
    private Long masterId;

    @Column(name = "response_to_id")
    private Long responseToId;

    @Column(name = "creation_date_time")
    private ZonedDateTime creationDateTime;

    @Column(name = "review_date_time")
    private ZonedDateTime reviewDateTime;

    @Column(name = "master_uuid", updatable = false)
    @org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID masterUuid;


    public CabinetMessage messageType(String messageType) {
        this.messageType = messageType;
        return this;
    }

    public CabinetMessage messageDirection(String messageDirection) {
        this.messageDirection = messageDirection;
        return this;
    }
    public CabinetMessage fromPortalSubscriberId(Long fromPortalSubscriberId) {
        this.fromPortalSubscriberId = fromPortalSubscriberId;
        return this;
    }
    public CabinetMessage fromPortalUserId(Long fromPortalUserId) {
        this.fromPortalUserId = fromPortalUserId;
        return this;
    }

    public CabinetMessage toPortalSubscriberId(Long toPortalSubscriberId) {
        this.toPortalSubscriberId = toPortalSubscriberId;
        return this;
    }

    public CabinetMessage toPortalUserId(Long toPortalUserId) {
        this.toPortalUserId = toPortalUserId;
        return this;
    }

    public CabinetMessage messageBody(String messageBody) {
        this.messageBody = messageBody;
        return this;
    }

    public CabinetMessage masterId(Long masterId) {
        this.masterId = masterId;
        return this;
    }
    public CabinetMessage responseToId(Long responseToId) {
        this.responseToId = responseToId;
        return this;
    }

    public CabinetMessage creationDateTime(ZonedDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
        return this;
    }
    public CabinetMessage reviewDateTime(ZonedDateTime reviewDateTime) {
        this.reviewDateTime = reviewDateTime;
        return this;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CabinetMessage cabinetMessage = (CabinetMessage) o;
        if (cabinetMessage.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), cabinetMessage.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CabinetMessage{" +
            "id=" + getId() +
            ", messageType='" + getMessageType() + "'" +
            ", messageDirection='" + getMessageDirection() + "'" +
            ", fromPortalSubscriberId='" + getFromPortalSubscriberId() + "'" +
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

    public UUID getMasterUuid() {
        return masterUuid;
    }

    public void setMasterUuid(UUID masterUuid) {
        this.masterUuid = masterUuid;
    }

}
