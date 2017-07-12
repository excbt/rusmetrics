package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "report_meta_param_directory")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
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

}
