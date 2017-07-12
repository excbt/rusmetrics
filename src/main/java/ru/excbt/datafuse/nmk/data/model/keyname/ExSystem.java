package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "ex_system")
@Getter
public class ExSystem extends AbstractKeynameEntity {


	/**
	 *
	 */
	private static final long serialVersionUID = 7700237687698913901L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "ex_system_name")
	private String name;

	@Column(name = "ex_system_comment")
	private String comment;

	@Version
	private int version;

}
