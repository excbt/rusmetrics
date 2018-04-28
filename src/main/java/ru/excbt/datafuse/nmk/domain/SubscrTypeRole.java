package ru.excbt.datafuse.nmk.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_type_role")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
@Immutable
public class SubscrTypeRole implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4889186302635036698L;

    @Id
    @Column
    private Long id;

    @ManyToOne
	@JoinColumn(name = "subscr_type")
	private SubscrType subscrType;

	@Column(name = "subscr_role_name")
	private String subscrRoleName;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscrTypeRole that = (SubscrTypeRole) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    public String toString() {
        return "SubscrTypeRole(id=" + this.getId() + ", subscrRoleName=" + this.getSubscrRoleName() + ", version=" + this.getVersion() + ", deleted=" + this.getDeleted() + ")";
    }
}
