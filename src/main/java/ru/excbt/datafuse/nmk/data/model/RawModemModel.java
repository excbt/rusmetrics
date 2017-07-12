package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "raw_modem_model")
@Getter
@Setter
public class RawModemModel extends JsonAbstractAuditableModel implements DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = 721037676058278008L;

	@Column(name = "raw_modem_type")
	private String rawModemType;

	@Column(name = "raw_modem_model_name")
	private String rawModemModelName;

	@Column(name = "raw_modem_model_comment")
	private String rawModemModelComment;

	@Column(name = "raw_modem_model_description")
	private String rawModemModelDescription;

	@Column(name = "raw_modem_model_identity")
	private String rawModemModelIdentity;

	@Column(name = "is_dialup")
	private Boolean isDialup;

	@Column(name = "dev_comment")
	private String devComment;

	@JsonIgnore
	@Column(name = "is_protected", insertable = false, updatable = false)
	private Boolean isProtected;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
