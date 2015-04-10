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

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(name="report_task")
public class ReportTask extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3985453988568530051L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	private Subscriber subscriber;

	@Column(name = "task_date")
	@Temporal (TemporalType.TIMESTAMP)
	private Date taskDate;
	
	@Column(name = "report_date")
	@Temporal (TemporalType.TIMESTAMP)
	private Date reportDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_template_id")
	private ReportTemplate reportTemplate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_paramset_id")
	private ReportParamset reportParamset;
	
	@Column(name="is_complete")
	private boolean _complete;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_shedule_id")
	private ReportShedule reportShedule;

	@Column(name = "report_data_begin_date")
	@Temporal (TemporalType.TIMESTAMP)
	private Date reportDataBeginDate;

	@Column(name = "report_data_end_date")
	@Temporal (TemporalType.TIMESTAMP)
	private Date reportDataEndDate;
	
	@Version
	private int version;

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public Date getTaskDate() {
		return taskDate;
	}

	public void setTaskDate(Date taskDate) {
		this.taskDate = taskDate;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public ReportTemplate getReportTemplate() {
		return reportTemplate;
	}

	public void setReportTemplate(ReportTemplate reportTemplate) {
		this.reportTemplate = reportTemplate;
	}

	public ReportParamset getReportParamset() {
		return reportParamset;
	}

	public void setReportParamset(ReportParamset reportParamset) {
		this.reportParamset = reportParamset;
	}

	public boolean is_complete() {
		return _complete;
	}

	public void set_complete(boolean _complete) {
		this._complete = _complete;
	}

	public ReportShedule getReportShedule() {
		return reportShedule;
	}

	public void setReportShedule(ReportShedule reportShedule) {
		this.reportShedule = reportShedule;
	}

	public Date getReportDataBeginDate() {
		return reportDataBeginDate;
	}

	public void setReportDataBeginDate(Date reportDataBeginDate) {
		this.reportDataBeginDate = reportDataBeginDate;
	}

	public Date getReportDataEndDate() {
		return reportDataEndDate;
	}

	public void setReportDataEndDate(Date reportDataEndDate) {
		this.reportDataEndDate = reportDataEndDate;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
}
