package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "raw_modem_type")
@Getter
public class RawModemType extends JsonAbstractKeynameEntity implements DeletedMarker {

	/**
	 *
	 */
	private static final long serialVersionUID = -8591532692074145572L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "description")
	private String description;

	@Column(name = "comment")
	private String comment;

	@Column(name = "raw_modem_type_name")
	private String rawModemTypeName;

	@Column(name = "dev_comment")
	private String devComment;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
