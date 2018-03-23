package ru.excbt.datafuse.nmk.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.config.Constants;
import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;
import ru.excbt.datafuse.nmk.data.domain.PersistableBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(schema = Constants.DB_SCHEME_PORTAL, name = "organization_type")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class OrganizationType extends AbstractPersistableEntity<Long> implements PersistableBuilder<OrganizationType, Long> {

    @Column(name = "type_keyname")
    private String typeKeyname;

    @Column(name = "type_name")
    private String typeName;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "deleted")
    private int deleted;

}
