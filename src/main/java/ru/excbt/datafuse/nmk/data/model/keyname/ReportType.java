package ru.excbt.datafuse.nmk.data.model.keyname;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.ReportMetaParamCommon;
import ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial;

@Entity
@Table(name = "report_type")
public class ReportType extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6608655097029684171L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "report_type_name")
	private String name;

	@Column(name = "report_type_description")
	private String description;

	@Column(name = "report_type_comment")
	private String comment;

	@Column(name = "report_type_suffix")
	private String suffix;

	@Column(name = "report_type_enabled")
	private boolean _enabled;

	@Version
	private int version;

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "reportType")
	private ReportMetaParamCommon reportMetaParamCommon;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "reportType")
	private Collection<ReportMetaParamSpecial> reportMetaParamSpecialList = new ArrayList<ReportMetaParamSpecial>();

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean is_enabled() {
		return _enabled;
	}

	public void set_enabled(boolean _enabled) {
		this._enabled = _enabled;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public ReportMetaParamCommon getReportMetaParamCommon() {
		return reportMetaParamCommon;
	}

	public void setReportMetaParamCommon(
			ReportMetaParamCommon reportMetaParamCommon) {
		this.reportMetaParamCommon = reportMetaParamCommon;
	}

	public Collection<ReportMetaParamSpecial> getReportMetaParamSpecialList() {
		return reportMetaParamSpecialList;
	}

	public void setReportMetaParamSpecialList(
			Collection<ReportMetaParamSpecial> reportMetaParamSpecialList) {
		this.reportMetaParamSpecialList = reportMetaParamSpecialList;
	}

}
