package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_pref")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
public class SubscrPref extends JsonAbstractKeynameEntity implements DisabledObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 322159788896491335L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "comment")
	private String comment;

	@Column(name = "subscr_pref_category")
	private String subscrPrefCategory;

	@Column(name = "pref_name")
	private String prefName;

	@Column(name = "pref_description")
	private String prefDescription;

	@Column(name = "pref_comment")
	private String prefComment;

	@Column(name = "pref_value_type")
	private String prefValueType;

	@Column(name = "pref_order")
	private Integer prefOrder;

	@Column(name = "dev_comment")
	private String devComment;

	@Column(name = "is_active_caption")
	private String isActiveCaption;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	@JsonIgnore
	@Column(name = "is_disabled")
	private Boolean isDisabled;

}
