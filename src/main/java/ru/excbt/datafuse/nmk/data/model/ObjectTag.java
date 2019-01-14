package ru.excbt.datafuse.nmk.data.model;


import ru.excbt.datafuse.nmk.data.domain.AbstractAuditingEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "object_tag")
@IdClass(ObjectTag.PK.class)
public class ObjectTag extends AbstractAuditingEntity implements Serializable {

    public static final String contObjectTagKeyname = "cont-object";
    public static final String contZPointTagKeyname = "cont-zpoint";
    public static final String deviceObjectTagKeyname = "device-object";


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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PK pk = (PK) o;
            return Objects.equals(subscriberId, pk.subscriberId) &&
                Objects.equals(objectTagKeyname, pk.objectTagKeyname) &&
                Objects.equals(objectId, pk.objectId) &&
                Objects.equals(tagName, pk.tagName);
        }

        @Override
        public int hashCode() {

            return Objects.hash(subscriberId, objectTagKeyname, objectId, tagName);
        }
    }

    @Id
    @Column(name = "subscriber_id")
    @NotNull
    private Long subscriberId;

    @Id
    @Column(name = "object_tag_keyname")
    @NotNull
    private String objectTagKeyname;

    @Id
    @Column(name = "object_id")
    @NotNull
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectTag objectTag = (ObjectTag) o;
        return Objects.equals(subscriberId, objectTag.subscriberId) &&
            Objects.equals(objectTagKeyname, objectTag.objectTagKeyname) &&
            Objects.equals(objectId, objectTag.objectId) &&
            Objects.equals(tagName, objectTag.tagName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(subscriberId, objectTagKeyname, objectId, tagName);
    }
}
