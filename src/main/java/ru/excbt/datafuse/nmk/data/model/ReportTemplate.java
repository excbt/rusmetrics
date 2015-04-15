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

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity 
@Table(name = "report_template")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportTemplate extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6041687194914761423L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	@JsonIgnore
	private Subscriber subscriber;

	@Enumerated(EnumType.STRING)
	@Column(name = "report_type")
	private ReportTypeKey reportTypeKey;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "report_type", insertable = false, updatable = false)
	private ReportType reportType;

	@Column(name = "report_template_name")
	private String name;

	@Column(name = "report_template_description")
	private String description;

	@Column(name = "report_template_comment")
	private String comment;

	@Column(name = "is_default")
	private boolean _default = false;

	@Column(name = "is_active")
	private boolean _active = false;

	@Column(name = "active_start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date activeStartDate;

	@Column(name = "active_end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date activeEndDate;

	@Column(name = "src_report_template_id")
	private Long srcReportTemplateId;
	
	@Version
	private int version;

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
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

	public ReportTypeKey getReportTypeKey() {
		return reportTypeKey;
	}

	public void setReportTypeKey(ReportTypeKey reportTypeKey) {
		this.reportTypeKey = reportTypeKey;
	}

	public Long getSrcReportTemplateId() {
		return srcReportTemplateId;
	}

	public void setSrcReportTemplateId(Long srcReportTemplateId) {
		this.srcReportTemplateId = srcReportTemplateId;
	}

	public boolean isCommon() {
		return this.subscriber == null;
	}

	public ReportType getReportType() {
		return reportType;
	}

	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}
	
}
