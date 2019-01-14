package ru.excbt.datafuse.nmk.data.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "object_tag_global")
@Getter
@Setter
@IdClass(ObjectTagGlobal.PK.class)
public class ObjectTagGlobal implements Serializable {

    @Getter
    @Setter
    public static final class PK implements Serializable {

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
            return Objects.equals(objectTagKeyname, pk.objectTagKeyname) &&
                Objects.equals(tagName, pk.tagName);
        }

        @Override
        public int hashCode() {

            return Objects.hash(objectTagKeyname, tagName);
        }
    }

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
        ObjectTagGlobal that = (ObjectTagGlobal) o;
        return Objects.equals(objectTagKeyname, that.objectTagKeyname) &&
            Objects.equals(tagName, that.tagName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(objectTagKeyname, tagName);
    }
}
