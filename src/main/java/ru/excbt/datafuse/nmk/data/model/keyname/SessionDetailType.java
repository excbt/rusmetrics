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
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "session_detail_type")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
public class SessionDetailType extends JsonAbstractKeynameEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -8354332014119723444L;

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
