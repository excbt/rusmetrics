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
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_type_pref")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class SubscrTypePref implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 9149973385103873472L;

    @Id
    @SequenceGenerator(name = "subscrTypePref", sequenceName = "seq_global_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscrTypePref")
    @Column
    private Long id;

    @Column(name = "subscr_type")
	private String subscrType;

	@Column(name = "subscr_pref")
	private String subscrPref;

	@Column(name = "dev_comment")
	private String devComment;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
