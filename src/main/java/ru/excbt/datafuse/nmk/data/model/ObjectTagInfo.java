package ru.excbt.datafuse.nmk.data.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "object_tag_info")
@Getter
@Setter
@IdClass(ObjectTagInfo.PK.class)
public class ObjectTagInfo implements Serializable, DeletedMarker {

    public static final String DEFAULT_COLOR = "DEFAULT";

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
    @NotNull
    private Long subscriberId;

    @Id
    @Column(name = "object_tag_keyname")
    @NotNull
    private String objectTagKeyname;

    @Id
    @Column(name = "tag_name")
    @NotNull
    private String tagName;

    @Column(name = "tag_color")
    private String tagColor = DEFAULT_COLOR;

    @Column(name = "tag_description")
    private String tagDescription;

    @Column(name = "tag_comment")
    private String tagComment;

    @Column(name = "is_enabled")
    @NotNull
    private Boolean isEnabled = true;

    @Column(name = "flex_data")
    @Type(type = "JsonbAsString")
    private String flexData;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;


    public ObjectTagInfo tagName(String tagName) {
        this.tagName = tagName;
        return this;
    }

    public ObjectTagInfo tagColor(String tagColor) {
        this.tagColor = tagColor;
        return this;
    }


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
