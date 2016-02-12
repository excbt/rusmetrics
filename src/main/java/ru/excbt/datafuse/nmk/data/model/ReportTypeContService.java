package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "report_type_cont_service")
@JsonIgnoreProperties(value = { "reportType", "reportTypeKeyname" })
public class ReportTypeContService extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6199350882623823558L;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_type", insertable = false, updatable = false)
	private ReportType reportType;

	@Column(name = "report_type")
	private String reportTypeKeyname;

	@Column(name = "cont_service_type")
	private String contServiceType;

	public ReportType getReportType() {
		return reportType;
	}

	public String getReportTypeKeyname() {
		return reportTypeKeyname;
	}

	public String getContServiceType() {
		return contServiceType;
	}

}