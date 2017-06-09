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
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.SessionDetailType;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "session_detail_type_cont_service_type")
@Getter
@Setter
public class SessionDetailTypeContServiceType extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = 3061671752355415024L;

	@Column(name = "cont_service_type", insertable = false, updatable = false)
	private String contServiceType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_detail_type", insertable = false, updatable = false)
	private SessionDetailType sessionDetailType;

	@Column(name = "order_idx", insertable = false, updatable = false)
	private Integer orderIdx;

	@Column(name = "caption")
	private String caption;

	@Column(name = "description")
	private String description;

	@Column(name = "comment")
	private String comment;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
