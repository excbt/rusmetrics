package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "report_type_cont_service_type")
@JsonIgnoreProperties(value = { "reportType", "reportTypeKeyname" })
@JsonInclude(Include.NON_NULL)
@Getter
public class ReportTypeContServiceType extends JsonAbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 6199350882623823558L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_type", insertable = false, updatable = false)
	private ReportType reportType;

	@Column(name = "report_type")
	private String reportTypeKeyname;

	@JsonUnwrapped
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_service_type")
	private ContServiceType contServiceType;

}
