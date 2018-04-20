package ru.excbt.datafuse.nmk.domain;

import javax.persistence.*;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_type")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
public class SubscrType extends JsonAbstractKeynameEntity implements Serializable, KeynameObject {

    private static final long serialVersionUID = -8443196795614375881L;

    @Id
    @Column(name = "keyname")
    private String keyname;

	@Column(name = "caption")
	private String caption;

	@Column(name = "subscr_type_name")
	private String subscrTypeName;

	@Column(name = "subscr_type_description")
	private String subscrTypeDescription;

	@Column(name = "subscr_type_comment")
	private String subscrTypeComment;

	@Column(name = "subscr_type_order")
	private Integer subscrTypeOrder;

	@Column(name = "is_rma")
	private Boolean isRma;

	@Column(name = "is_child")
	private Boolean isChild;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubscrType that = (SubscrType) o;
        return Objects.equals(keyname, that.keyname);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), keyname);
    }
}
