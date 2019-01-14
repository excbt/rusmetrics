package ru.excbt.datafuse.nmk.data.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_zpoint_cons_field")
@Getter
@Setter
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ContZPointConsField implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cont_zpoint_id")
    @NotNull
    private ContZPoint contZPoint;

    @Id
    @Column(name = "field_name")
    @NotNull
    private String fieldName;

    @Column(name = "is_enabled")
    @NotNull
    private Boolean isEnabled = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContZPointConsField that = (ContZPointConsField) o;
        return Objects.equals(contZPoint, that.contZPoint) &&
            Objects.equals(fieldName, that.fieldName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(contZPoint, fieldName);
    }
}
