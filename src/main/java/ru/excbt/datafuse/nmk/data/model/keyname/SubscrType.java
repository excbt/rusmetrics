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
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_type")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
public class SubscrType extends JsonAbstractKeynameEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -2818498827737120028L;

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

}
