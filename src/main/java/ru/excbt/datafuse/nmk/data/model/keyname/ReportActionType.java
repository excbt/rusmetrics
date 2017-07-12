package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name="report_action_type")
@Getter
public class ReportActionType extends AbstractKeynameEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1940624764909338156L;

	@Column(name="caption")
	private String caption;

	@Column(name="report_action_type_name")
	private String name;

	@Column(name="report_action_type_description")
	private String description;

	@Column(name="report_action_type_comment")
	private String comment;

	@Version
	private int version;

}
