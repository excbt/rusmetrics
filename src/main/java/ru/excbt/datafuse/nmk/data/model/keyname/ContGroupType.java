package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_group_type")
@Getter
public class ContGroupType extends AbstractKeynameEntity {

	/**
		 *
		 */
	private static final long serialVersionUID = 8527718550849111235L;

	@Column(name = "caption")
	private String contGroupTypeCaption;

	@Column(name = "type_name")
	private String contGroupTypeName;

	@Column(name = "type_comment")
	private String contGroupTypeComment;

	@Column(name = "type_description")
	private String contGroupTypeDescription;

	@Version
	private int version;

}
