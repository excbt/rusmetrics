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

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "report_meta_param_category")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
public class ReportMetaParamCategory extends JsonAbstractKeynameEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 6958456671215597934L;

	@Column(name = "caption")
	private String caption;

	@JsonIgnore
	@Column(name = "description")
	private String description;

	@JsonIgnore
	@Column(name = "category_comment")
	private String categoryComment;

	@Column(name = "category_order")
	private Integer categoryOrder;

	@JsonIgnore
	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
