package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_pref_category")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
public class SubscrPrefCategory extends JsonAbstractKeynameEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1174728306982156515L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "category_name")
	private String categoryName;

	@Column(name = "category_description")
	private String categoryDescription;

	@Column(name = "category_comment")
	private String categoryComment;

	@Column(name = "category_order")
	private Integer categoryOrder;

	@Column(name = "dev_comment")
	private String devComment;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
