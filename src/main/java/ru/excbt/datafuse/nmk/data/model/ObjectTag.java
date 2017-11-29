package ru.excbt.datafuse.nmk.data.model;


import ru.excbt.datafuse.nmk.data.domain.AbstractAuditingEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "object_tag")
@IdClass(ObjectTag.PK.class)
public class ObjectTag extends AbstractAuditingEntity implements Serializable {


    public static final class PK implements Serializable {

        @Column(name = "subscriber_id")
        @NotNull
        private Long subscriberId;

        @Column(name = "object_tag_keyname")
        @NotNull
        private String objectTagKeyname;

        @Column(name = "object_id")
        @NotNull
        private Long objectId;

        @Column(name = "tag_name")
        @NotNull
        private String tagName;


        public Long getSubscriberId() {
            return subscriberId;
        }

        public void setSubscriberId(Long subscriberId) {
            this.subscriberId = subscriberId;
        }

        public String getObjectTagKeyname() {
            return objectTagKeyname;
        }

        public void setObjectTagKeyname(String objectTagKeyname) {
            this.objectTagKeyname = objectTagKeyname;
        }

        public Long getObjectId() {
            return objectId;
        }

        public void setObjectId(Long objectId) {
            this.objectId = objectId;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }
    }

    @Id
    @Column(name = "subscriber_id")
    private Long subscriberId;

    @Id
    @Column(name = "object_tag_keyname")
    private String objectTagKeyname;

    @Id
    @Column(name = "object_id")
    private Long objectId;

    @Id
    @Column(name = "tag_name")
    private String tagName;


    public ObjectTag tagName(String tagName) {
        this.tagName = tagName;
        return this;
    }

    public ObjectTag objectId(Long id) {
        this.objectId = id;
        return this;
    }

    public ObjectTag objectId(Integer id) {
        this.objectId = Long.valueOf(id);
        return this;
    }


    public Long getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getObjectTagKeyname() {
        return objectTagKeyname;
    }

    public void setObjectTagKeyname(String objectTagKeyname) {
        this.objectTagKeyname = objectTagKeyname;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
