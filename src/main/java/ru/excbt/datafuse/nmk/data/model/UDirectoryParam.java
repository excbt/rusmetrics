package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Параметры универсального справочника
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.03.2015
 *
 */
@Entity
@Table(name = "u_directory_param")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class UDirectoryParam extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -3859570295447832844L;

	@Version
	@Column
	private int version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "directory_id")
	@JsonIgnore
	private UDirectory directory;

	@Column(name = "param_type")
	private String paramType;

	@Column(name = "param_name")
	private String paramName;

}
