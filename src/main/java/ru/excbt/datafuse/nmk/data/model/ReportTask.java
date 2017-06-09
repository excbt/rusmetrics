package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Задание на создание отчета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.04.2015
 *
 */
@Entity
@Table(name = "report_task")
@Getter
@Setter
public class ReportTask extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 3985453988568530051L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	private Subscriber subscriber;

	@Column(name = "task_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date taskDate;

	@Column(name = "report_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date reportDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_template_id")
	private ReportTemplate reportTemplate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_paramset_id")
	private ReportParamset reportParamset;

	@Column(name = "is_complete")
	private boolean _complete;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_shedule_id")
	private ReportShedule reportShedule;

	@Column(name = "report_data_begin_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date reportDataBeginDate;

	@Column(name = "report_data_end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date reportDataEndDate;

	@Version
	private int version;

}
