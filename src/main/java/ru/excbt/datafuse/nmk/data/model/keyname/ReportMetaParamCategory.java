package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "report_meta_param_category")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
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

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategoryComment() {
		return categoryComment;
	}

	public void setCategoryComment(String categoryComment) {
		this.categoryComment = categoryComment;
	}

	public Integer getCategoryOrder() {
		return categoryOrder;
	}

	public void setCategoryOrder(Integer categoryOrder) {
		this.categoryOrder = categoryOrder;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

}
