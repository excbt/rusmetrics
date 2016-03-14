package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "report_meta_param_directory")
public class ReportMetaParamDirectory extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "directory_description")
	private String directoryDescription;

	@JsonIgnore
	@Column(name = "directory_comment")
	private String directoryComment;

	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDirectoryDescription() {
		return directoryDescription;
	}

	public void setDirectoryDescription(String directoryDescription) {
		this.directoryDescription = directoryDescription;
	}

	public String getDirectoryComment() {
		return directoryComment;
	}

	public void setDirectoryComment(String directoryComment) {
		this.directoryComment = directoryComment;
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
