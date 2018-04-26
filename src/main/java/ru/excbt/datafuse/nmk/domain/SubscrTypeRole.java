package ru.excbt.datafuse.nmk.domain;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

import java.io.Serializable;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_type_role")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class SubscrTypeRole implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4889186302635036698L;

    @Id
    @SequenceGenerator(name = "subscrTypeRoleSeq", sequenceName = "seq_global_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscrTypeRoleSeq")
    @Column
    private Long id;

	@Column(name = "subscr_type")
	private String subscrType;

	@Column(name = "subscr_role_name")
	private String subscrRoleName;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
