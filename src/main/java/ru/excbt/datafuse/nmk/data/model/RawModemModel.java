package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "raw_modem_model")
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

	@Column(name = "is_dialup")
	private Boolean isDialup;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public String getRawModemType() {
		return rawModemType;
	}

	public void setRawModemType(String rawModemType) {
		this.rawModemType = rawModemType;
	}

	public String getRawModemModelName() {
		return rawModemModelName;
	}

	public void setRawModemModelName(String rawModemModelName) {
		this.rawModemModelName = rawModemModelName;
	}

	public String getRawModemModelComment() {
		return rawModemModelComment;
	}

	public void setRawModemModelComment(String rawModemModelComment) {
		this.rawModemModelComment = rawModemModelComment;
	}

	public String getRawModemModelDescription() {
		return rawModemModelDescription;
	}

	public void setRawModemModelDescription(String rawModemModelDescription) {
		this.rawModemModelDescription = rawModemModelDescription;
	}

	public Boolean getIsDialup() {
		return isDialup;
	}

	public void setIsDialup(Boolean isDialup) {
		this.isDialup = isDialup;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

}