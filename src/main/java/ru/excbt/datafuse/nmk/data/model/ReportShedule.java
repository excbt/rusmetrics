package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportActionType;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportSheduleType;
import ru.excbt.datafuse.nmk.report.ReportActionKey;
import ru.excbt.datafuse.nmk.report.ReportSheduleTypeKey;

/**
 * Расписание создания и рассылки отчетов
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.04.2015
 *
 */
@Entity
@Table(name = "report_shedule")
@SQLDelete(sql = "UPDATE report_shedule SET deleted = 1 WHERE id = ? and version = ?")
@Where(clause = "deleted <> 1")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ReportShedule extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 2213169218870112017L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	@JsonIgnore
	private Subscriber subscriber;

	@Column(name = "subscriber_id", insertable = false, updatable = false)
	private Long subscriberId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "report_template_id")
	private ReportTemplate reportTemplate;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "report_paramset_id")
	private ReportParamset reportParamset;

	@Enumerated(EnumType.STRING)
	@Column(name = "report_shedule_type")
	private ReportSheduleTypeKey reportSheduleTypeKey;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "report_shedule_type", insertable = false, updatable = false)
	private ReportSheduleType reportSheduleType;

	@Column(name = "report_shedule_time_template")
	private String sheduleTimeTemplate;

	@Column(name = "report_shedule_start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date sheduleStartDate;

	@Column(name = "report_shedule_end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date sheduleEndDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "report_shedule_action1")
	private ReportActionKey sheduleAction1Key;

	@Enumerated(EnumType.STRING)
	@Column(name = "report_shedule_action2")
	private ReportActionKey sheduleAction2Key;

	@Enumerated(EnumType.STRING)
	@Column(name = "report_shedule_action3")
	private ReportActionKey sheduleAction3Key;

	@Enumerated(EnumType.STRING)
	@Column(name = "report_shedule_action4")
	private ReportActionKey sheduleAction4Key;

	@Enumerated(EnumType.STRING)
	@Column(name = "report_shedule_action5")
	private ReportActionKey sheduleAction5Key;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "report_shedule_action1", insertable = false, updatable = false)
	private ReportActionType sheduleAction1;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "report_shedule_action2", insertable = false, updatable = false)
	private ReportActionType sheduleAction2;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "report_shedule_action3", insertable = false, updatable = false)
	private ReportActionType sheduleAction3;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "report_shedule_action4", insertable = false, updatable = false)
	private ReportActionType sheduleAction4;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "report_shedule_action5", insertable = false, updatable = false)
	private ReportActionType sheduleAction5;

	@Column(name = "report_shedule_action1_param")
	private String sheduleAction1Param;

	@Column(name = "report_shedule_action2_param")
	private String sheduleAction2Param;

	@Column(name = "report_shedule_action3_param")
	private String sheduleAction3Param;

	@Column(name = "report_shedule_action4_param")
	private String sheduleAction4Param;

	@Column(name = "report_shedule_action5_param")
	private String sheduleAction5Param;

	@Column(name = "report_shedule_name")
	private String name;

	@Column(name = "report_shedule_comment")
	private String comment;

	@Column(name = "report_shedule_description")
	private String description;

	@Version
	private int version;

}
