package ru.excbt.datafuse.nmk.data.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "object_tag_info")
@Getter
@Setter
@IdClass(ObjectTagInfo.PK.class)
public class ObjectTagInfo implements Serializable {

    @Getter
    @Setter
    public static final class PK implements Serializable {

        @Column(name = "subscriber_id")
        @NotNull
        private Long subscriberId;

        @Column(name = "object_tag_keyname")
        @NotNull
        private String objectTagKeyname;


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
                Objects.equals(tagName, pk.tagName);
        }

        @Override
        public int hashCode() {

            return Objects.hash(subscriberId, objectTagKeyname, tagName);
        }
    }

    @Id
    @Column(name = "subscriber_id")
    private Long subscriberId;

    @Id
    @Column(name = "object_tag_keyname")
    private String objectTagKeyname;

    @Id
    @Column(name = "tag_name")
    private String tagName;

    @Column(name = "tag_color")
    private String tagColor;

    @Column(name = "tag_description")
    private String tagDescription;

    @Column(name = "tag_comment")
    private String tagComment;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Column(name = "flex_data")
    @Type(type = "JsonbAsString")
    private String flexData;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectTagInfo that = (ObjectTagInfo) o;
        return Objects.equals(subscriberId, that.subscriberId) &&
            Objects.equals(objectTagKeyname, that.objectTagKeyname) &&
            Objects.equals(tagName, that.tagName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(subscriberId, objectTagKeyname, tagName);
    }
}
