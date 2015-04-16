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

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportActionKey;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportSheduleTypeKey;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportActionType;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportSheduleType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "report_shedule")
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

	@Column(name = "report_shedule_comment")
	private String comment;

	@Column(name = "report_shedule_description")
	private String description;

	@Version
	private int version;

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

	public String getSheduleTimeTemplate() {
		return sheduleTimeTemplate;
	}

	public void setSheduleTimeTemplate(String sheduleTimeTemplate) {
		this.sheduleTimeTemplate = sheduleTimeTemplate;
	}

	public Date getSheduleStartDate() {
		return sheduleStartDate;
	}

	public void setSheduleStartDate(Date sheduleStartDate) {
		this.sheduleStartDate = sheduleStartDate;
	}

	public Date getSheduleEndDate() {
		return sheduleEndDate;
	}

	public void setSheduleEndDate(Date sheduleEndDate) {
		this.sheduleEndDate = sheduleEndDate;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public ReportSheduleTypeKey getReportSheduleTypeKey() {
		return reportSheduleTypeKey;
	}

	public void setReportSheduleTypeKey(
			ReportSheduleTypeKey reportSheduleTypeKey) {
		this.reportSheduleTypeKey = reportSheduleTypeKey;
	}

	public ReportSheduleType getReportSheduleType() {
		return reportSheduleType;
	}

	public void setReportSheduleType(ReportSheduleType reportSheduleType) {
		this.reportSheduleType = reportSheduleType;
	}

	public ReportActionKey getSheduleAction1Key() {
		return sheduleAction1Key;
	}

	public void setSheduleAction1Key(ReportActionKey sheduleAction1Key) {
		this.sheduleAction1Key = sheduleAction1Key;
	}

	public ReportActionKey getSheduleAction2Key() {
		return sheduleAction2Key;
	}

	public void setSheduleAction2Key(ReportActionKey sheduleAction2Key) {
		this.sheduleAction2Key = sheduleAction2Key;
	}

	public ReportActionKey getSheduleAction3Key() {
		return sheduleAction3Key;
	}

	public void setSheduleAction3Key(ReportActionKey sheduleAction3Key) {
		this.sheduleAction3Key = sheduleAction3Key;
	}

	public ReportActionKey getSheduleAction4Key() {
		return sheduleAction4Key;
	}

	public void setSheduleAction4Key(ReportActionKey sheduleAction4Key) {
		this.sheduleAction4Key = sheduleAction4Key;
	}

	public ReportActionKey getSheduleAction5Key() {
		return sheduleAction5Key;
	}

	public void setSheduleAction5Key(ReportActionKey sheduleAction5Key) {
		this.sheduleAction5Key = sheduleAction5Key;
	}

	public ReportActionType getSheduleAction1() {
		return sheduleAction1;
	}

	public void setSheduleAction1(ReportActionType sheduleAction1) {
		this.sheduleAction1 = sheduleAction1;
	}

	public ReportActionType getSheduleAction2() {
		return sheduleAction2;
	}

	public void setSheduleAction2(ReportActionType sheduleAction2) {
		this.sheduleAction2 = sheduleAction2;
	}

	public ReportActionType getSheduleAction3() {
		return sheduleAction3;
	}

	public void setSheduleAction3(ReportActionType sheduleAction3) {
		this.sheduleAction3 = sheduleAction3;
	}

	public ReportActionType getSheduleAction4() {
		return sheduleAction4;
	}

	public void setSheduleAction4(ReportActionType sheduleAction4) {
		this.sheduleAction4 = sheduleAction4;
	}

	public ReportActionType getSheduleAction5() {
		return sheduleAction5;
	}

	public void setSheduleAction5(ReportActionType sheduleAction5) {
		this.sheduleAction5 = sheduleAction5;
	}

	public String getSheduleAction1Param() {
		return sheduleAction1Param;
	}

	public void setSheduleAction1Param(String sheduleAction1Param) {
		this.sheduleAction1Param = sheduleAction1Param;
	}

	public String getSheduleAction2Param() {
		return sheduleAction2Param;
	}

	public void setSheduleAction2Param(String sheduleAction2Param) {
		this.sheduleAction2Param = sheduleAction2Param;
	}

	public String getSheduleAction3Param() {
		return sheduleAction3Param;
	}

	public void setSheduleAction3Param(String sheduleAction3Param) {
		this.sheduleAction3Param = sheduleAction3Param;
	}

	public String getSheduleAction4Param() {
		return sheduleAction4Param;
	}

	public void setSheduleAction4Param(String sheduleAction4Param) {
		this.sheduleAction4Param = sheduleAction4Param;
	}

	public String getSheduleAction5Param() {
		return sheduleAction5Param;
	}

	public void setSheduleAction5Param(String sheduleAction5Param) {
		this.sheduleAction5Param = sheduleAction5Param;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
