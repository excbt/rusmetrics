package ru.excbt.datafuse.nmk.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import ru.excbt.datafuse.nmk.config.Constants;
import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;
import ru.excbt.datafuse.nmk.data.domain.ModelIdable;
import ru.excbt.datafuse.nmk.data.domain.PersistableBuilder;
import ru.excbt.datafuse.nmk.domain.datatype.SortableData;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(schema = Constants.DB_SCHEME_PORTAL, name = "organization_type")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class OrganizationType implements Serializable, PersistableBuilder<OrganizationType, Long>,
    SortableData, ModelIdable<Long> {

    private static final long serialVersionUID = 7221168076509080892L;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "organizationTypeSeq", sequenceName = "seq_global_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizationTypeSeq")
    private Long id;

    @Column(name = "type_keyname")
    private String typeKeyname;

    @Column(name = "type_name")
    private String typeName;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "deleted")
    private int deleted;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "flex_ui")
    @Type(type = "JsonbAsString")
    private String flexUI;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationType that = (OrganizationType) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
