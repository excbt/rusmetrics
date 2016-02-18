package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Задание на рассылку отчета
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.04.2015
 *
 */
@Entity
@Table (name = "report_task_complete_action")
public class ReportTaskCompleteAction extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7012836234707188462L;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn (name = "report_task_id")
	@JsonIgnore
	private ReportTask reportTask;

	@Column (name = "report_action_type")
	private String reportActionType;

	@Column (name = "report_action_param")
	private String reportActionParam;

	@Column (name = "is_complete")
	private boolean _complete;

	@Version
	private int version;

	public ReportTask getReportTask() {
		return reportTask;
	}

	public void setReportTask(ReportTask reportTask) {
		this.reportTask = reportTask;
	}

	public String getReportActionType() {
		return reportActionType;
	}

	public void setReportActionType(String reportActionType) {
		this.reportActionType = reportActionType;
	}

	public String getReportActionParam() {
		return reportActionParam;
	}

	public void setReportActionParam(String reportActionParam) {
		this.reportActionParam = reportActionParam;
	}

	public boolean is_complete() {
		return _complete;
	}

	public void set_complete(boolean _complete) {
		this._complete = _complete;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
