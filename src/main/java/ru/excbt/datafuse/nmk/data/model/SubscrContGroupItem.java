package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Элементы группы
 *
 * @author S.Kuzovoy
 * @version 1.0
 * @since 27.05.2015
 *
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_cont_group_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class SubscrContGroupItem extends AbstractAuditableModel {

	/**
		 *
		 */
	private static final long serialVersionUID = 6212870140304523057L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscr_cont_group_id")
	private SubscrContGroup contGroup;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cont_object_id", insertable = false, updatable = false)
	private ContObject contObject;

	@Column(name = "cont_object_id")
	private Long contObjectId;

	@Version
	private int version;

}
