package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "raw_modem_type")
public class RawModemType extends JsonAbstractKeynameEntity {

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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getRawModemTypeName() {
		return rawModemTypeName;
	}

	public void setRawModemTypeName(String rawModemTypeName) {
		this.rawModemTypeName = rawModemTypeName;
	}

	public String getDevComment() {
		return devComment;
	}

	public void setDevComment(String devComment) {
		this.devComment = devComment;
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
