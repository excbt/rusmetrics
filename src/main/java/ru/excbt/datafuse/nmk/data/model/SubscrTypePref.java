package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_type_pref")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class SubscrTypePref extends JsonAbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 9149973385103873472L;

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
