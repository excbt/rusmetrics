package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Группа объектов для отчета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.04.2015
 *
 */
@Entity
@Table(name = "report_paramset_unit")
@SQLDelete(sql = "UPDATE report_paramset_unit SET deleted = 1 WHERE id = ? and version = ?")
@Where(clause = "deleted <> 1")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class ReportParamsetUnit extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 107534288349056624L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_paramset_id")
	@JsonIgnore
	private ReportParamset reportParamset;

	@Column(name = "object_id")
	private Long objectId;

	@Version
	private int version;

}
