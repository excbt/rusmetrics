package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "help_context")
public class HelpContext extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 152254610219938558L;

	@Column(name = "anchor_id")
	private String anchorId;

	@Column(name = "help_url")
	private String helpUrl;

	@Column(name = "help_category")
	private String helpCategory;

	@Column(name = "help_action")
	private String helpAction;

	@JsonIgnore
	@Column(name = "help_description")
	private String helpDescription;

	@JsonIgnore
	@Column(name = "help_comment")
	private String helpComment;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	public String getAnchorId() {
		return anchorId;
	}

	public void setAnchorId(String anchorId) {
		this.anchorId = anchorId;
	}

	public String getHelpUrl() {
		return helpUrl;
	}

	public void setHelpUrl(String helpUrl) {
		this.helpUrl = helpUrl;
	}

	public String getHelpCategory() {
		return helpCategory;
	}

	public void setHelpCategory(String helpCategory) {
		this.helpCategory = helpCategory;
	}

	public String getHelpAction() {
		return helpAction;
	}

	public void setHelpAction(String helpAction) {
		this.helpAction = helpAction;
	}

	public String getHelpDescription() {
		return helpDescription;
	}

	public void setHelpDescription(String helpDescription) {
		this.helpDescription = helpDescription;
	}

	public String getHelpComment() {
		return helpComment;
	}

	public void setHelpComment(String helpComment) {
		this.helpComment = helpComment;
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
