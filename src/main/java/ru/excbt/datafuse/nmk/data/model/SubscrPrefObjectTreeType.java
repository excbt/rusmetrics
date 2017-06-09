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
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_pref_object_tree_type")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class SubscrPrefObjectTreeType extends JsonAbstractAuditableModel implements DisabledObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -5446111068809742392L;

	@Column(name = "subscr_pref")
	private String subscrPrefKeyname;

	@Column(name = "object_tree_type")
	private String objectTreeType;

	@Column(name = "dev_comment")
	private String devComment;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	public String getObjectTreeType() {
		return objectTreeType;
	}

}
