package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "report_meta_param_directory_item")
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class ReportMetaParamDirectoryItem extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = -7572229023763561058L;

	@JsonIgnore
	@Column(name = "param_directory")
	private String paramDirectoryKeyname;

	@Column(name = "keyname")
	private String keyname;

	@Column(name = "caption")
	private String caption;

	@Column(name = "item_description")
	private String itemDescription;

	@JsonIgnore
	@Column(name = "item_comment")
	private String itemComment;

	@Column(name = "item_order")
	private Integer itemOrder;

	@Column(name = "is_default")
	private Boolean isDefault;

	@JsonIgnore
	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

}
