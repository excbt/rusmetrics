package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;

/**
 * Фильтр объектов для отчета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 02.11.2015
 *
 */
@Entity
@Table(name = "v_report_paramset_unit_filter")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class ReportParamsetUnitFilter extends AbstractPersistableEntity<Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = 8161581270266957433L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_paramset_id")
	@JsonIgnore
	private ReportParamset reportParamset;

	@Column(name = "object_id")
	private Long objectId;

	@Version
	private int version;

}
