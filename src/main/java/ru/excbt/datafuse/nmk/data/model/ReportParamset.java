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

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportOutputFileType;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportPeriodKey;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportPeriod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "report_paramset")
@SQLDelete(sql="UPDATE report_paramset SET deleted = 1 WHERE id = ? and version = ?")
@Where(clause="deleted <> 1")
@JsonIgnoreProperties (ignoreUnknown = true)
public class ReportParamset extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4162460506388144170L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "report_template_id")
	private ReportTemplate reportTemplate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	@JsonIgnore
	private Subscriber subscriber;	
	
	@Column (name = "report_paramset_name")
	private String name;

	@Column (name = "report_paramset_description")
	private String description;

	@Column (name = "report_paramset_comment")
	private String comment;

	@Enumerated(EnumType.STRING)	
	@Column (name = "output_file_type")
	private ReportOutputFileType outputFileType;

	@Column (name = "output_file_name_template")
	private String outputFileNameTemplate;
	
	@ManyToOne(fetch = FetchType.EAGER)	
	@JoinColumn(name="report_period", insertable = false, updatable = false)
	private ReportPeriod reportPeriod;

	@Enumerated(EnumType.STRING)	
	@Column(name="report_period")
	private ReportPeriodKey reportPeriodKey;
	
	@Column(name = "report_paramset_date")
	@Temporal (TemporalType.TIMESTAMP)
	private Date paramsetDate;
	
	@Column(name = "report_paramset_start_date")
	@Temporal (TemporalType.TIMESTAMP)
	private Date paramsetStartDate;
	
	@Column(name = "report_paramset_end_date")
	@Temporal (TemporalType.TIMESTAMP)
	private Date paramsetEndDate;
	
	@Column(name = "is_default")
	private boolean _default;

	@Column(name = "is_active")
	private boolean _active;
	
	@Column(name = "active_start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date activeStartDate;

	@Column(name = "active_end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date activeEndDate;

	@Column(name = "src_report_paramset_id")
	private Long srcReportParamsetId;
	
	@Version
	private int version;
	
	public ReportTemplate getReportTemplate() {
		return reportTemplate;
	}

	public void setReportTemplate(ReportTemplate reportTemplate) {
		this.reportTemplate = reportTemplate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ReportOutputFileType getOutputFileType() {
		return outputFileType;
	}

	public void setOutputFileType(ReportOutputFileType outputFileType) {
		this.outputFileType = outputFileType;
	}


	public Date getParamsetDate() {
		return paramsetDate;
	}

	public void setParamsetDate(Date paramsetDate) {
		this.paramsetDate = paramsetDate;
	}

	public Date getParamsetStartDate() {
		return paramsetStartDate;
	}

	public void setParamsetStartDate(Date paramsetStartDate) {
		this.paramsetStartDate = paramsetStartDate;
	}

	public Date getParamsetEndDate() {
		return paramsetEndDate;
	}

	public void setParamsetEndDate(Date paramsetEndDate) {
		this.paramsetEndDate = paramsetEndDate;
	}

	public boolean is_default() {
		return _default;
	}

	public void set_default(boolean _default) {
		this._default = _default;
	}

	public boolean is_active() {
		return _active;
	}

	public void set_active(boolean _active) {
		this._active = _active;
	}

	public Date getActiveStartDate() {
		return activeStartDate;
	}

	public void setActiveStartDate(Date activeStartDate) {
		this.activeStartDate = activeStartDate;
	}

	public Date getActiveEndDate() {
		return activeEndDate;
	}

	public void setActiveEndDate(Date activeEndDate) {
		this.activeEndDate = activeEndDate;
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

	public Long getSrcReportParamsetId() {
		return srcReportParamsetId;
	}

	public void setSrcReportParamsetId(Long srcReportParamsetId) {
		this.srcReportParamsetId = srcReportParamsetId;
	}

	public ReportPeriod getReportPeriod() {
		return reportPeriod;
	}


	public ReportPeriodKey getReportPeriodKey() {
		return reportPeriodKey;
	}

	public void setReportPeriodKey(ReportPeriodKey reportPeriodKey) {
		this.reportPeriodKey = reportPeriodKey;
	}

	public boolean isCommon() {
		return this.subscriber == null;
	}

	public String getOutputFileNameTemplate() {
		return outputFileNameTemplate;
	}

	public void setOutputFileNameTemplate(String outputFileNameTemplate) {
		this.outputFileNameTemplate = outputFileNameTemplate;
	}

	public void setReportPeriod(ReportPeriod reportPeriod) {
		this.reportPeriod = reportPeriod;
	}
	
}
