package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_object_tree_cont_object")
@Getter
@Setter
public class SubscrObjectTreeContObject extends JsonAbstractAuditableModel implements DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = -8235602462750786719L;

	@Column(name = "subscr_object_tree_id")
	private Long subscrObjectTreeId;

	@Column(name = "cont_object_id")
	private Long contObjectId;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
