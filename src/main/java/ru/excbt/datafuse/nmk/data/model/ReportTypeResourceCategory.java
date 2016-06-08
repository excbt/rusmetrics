package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import net.minidev.json.annotate.JsonIgnore;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "report_type_resource_category")
public class ReportTypeResourceCategory extends JsonAbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3177659198880090613L;

	@Column(name = "resource_category")
	private String resourceCategory;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_type")
	private ReportType reportType;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public String getResourceCategory() {
		return resourceCategory;
	}

	public ReportType getReportType() {
		return reportType;
	}

	public int getVersion() {
		return version;
	}

	public int getDeleted() {
		return deleted;
	}

}