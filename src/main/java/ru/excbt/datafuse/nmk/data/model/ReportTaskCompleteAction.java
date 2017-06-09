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
@Getter
@Setter
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

}
