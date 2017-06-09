package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "help_context")
@Getter
@Setter
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

}
