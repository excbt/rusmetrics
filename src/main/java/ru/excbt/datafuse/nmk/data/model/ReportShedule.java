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
@Table(name="report_shedule")
public class ReportShedule extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2213169218870112017L;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn (name = "subscriber_id")
	private Subscriber subscriber;
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn (name = "report_template_id")
	private ReportTemplate reportTemplate;
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn (name = "report_paramset_id")
	private ReportParamset reportParamset;
	
	@Column(name = "report_shedule_type")
	private String reportSheduleType;
	
	@Column(name = "report_shedule_time_template")
	private String sheduleTimeTemplate;
	
	@Column(name = "report_shedule_start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date sheduleStartDate;

	@Column(name = "report_shedule_end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date sheduleEndDate;
	
	@Column(name = "report_shedule_action1")	
	private String sheduleAction1;

	@Column(name = "report_shedule_action2")	
	private String sheduleAction2;
	
	@Column(name = "report_shedule_action3")	
	private String sheduleAction3;
	
	@Column(name = "report_shedule_action4")	
	private String sheduleAction4;
	
	@Column(name = "report_shedule_action5")	
	private String sheduleAction5;

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

	public String getReportSheduleType() {
		return reportSheduleType;
	}

	public void setReportSheduleType(String reportSheduleType) {
		this.reportSheduleType = reportSheduleType;
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

	public String getSheduleAction1() {
		return sheduleAction1;
	}

	public void setSheduleAction1(String sheduleAction1) {
		this.sheduleAction1 = sheduleAction1;
	}

	public String getSheduleAction2() {
		return sheduleAction2;
	}

	public void setSheduleAction2(String sheduleAction2) {
		this.sheduleAction2 = sheduleAction2;
	}

	public String getSheduleAction3() {
		return sheduleAction3;
	}

	public void setSheduleAction3(String sheduleAction3) {
		this.sheduleAction3 = sheduleAction3;
	}

	public String getSheduleAction4() {
		return sheduleAction4;
	}

	public void setSheduleAction4(String sheduleAction4) {
		this.sheduleAction4 = sheduleAction4;
	}

	public String getSheduleAction5() {
		return sheduleAction5;
	}

	public void setSheduleAction5(String sheduleAction5) {
		this.sheduleAction5 = sheduleAction5;
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
}
