package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "report_meta_param_special_type")
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
public class ReportMetaParamSpecialType extends AbstractKeynameEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1513108600723081299L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "special_type_name")
	private String specialTypeName;

	@JsonIgnore
	@Column(name = "special_type_description")
	private String specialTypeDescription;

	@JsonIgnore
	@Column(name = "special_type_comment")
	private String specialTypeComment;

	@Column(name = "special_type_directory")
	private String specialTypeDirectory;

	@Column(name = "special_type_directory_url")
	private String specialTypeDirectoryUrl;

	@Column(name = "special_type_directory_key")
	private String specialTypeDirectoryKey;

	@Column(name = "special_type_directory_caption")
	private String specialTypeDirectoryCaption;

	@Column(name = "special_type_directory_value")
	private String specialTypeDirectoryValue;

	@Column(name = "special_type_field1")
	private String specialTypeField1;

	@Column(name = "special_type_field2")
	private String specialTypeField2;

	@JsonIgnore
	@Version
	private int version;

}
