package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DevModeObject;
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

@Entity
@Table(name = "report_type")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ReportType extends AbstractKeynameEntity implements DevModeObject, DisabledObject {

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

	@Column(name = "is_dev_mode")
	private Boolean isDevMode;

	@Column(name = "preview_url")
	private String previewUrl;

	@Version
	private int version;

	//	@OneToOne(fetch = FetchType.EAGER, mappedBy = "reportType")
	//	private ReportMetaParamCommon reportMetaParamCommon;

	//	@OneToMany(fetch = FetchType.EAGER, mappedBy = "reportType")
	//	private List<ReportMetaParamSpecial> reportMetaParamSpecialList = new ArrayList<ReportMetaParamSpecial>();

	@Column(name = "report_type_order")
	private Integer reportTypeOrder;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@Column(name = "report_system")
	private String reportSystem;

	@Column(name = "report_category")
	private String reportCategory;

	@Column(name = "resource_category")
	private String resourceCategory;

	public String getCaption() {
		return caption;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getComment() {
		return comment;
	}

	public String getSuffix() {
		return suffix;
	}

	public boolean is_enabled() {
		return _enabled;
	}

	@Override
	public Boolean getIsDevMode() {
		return isDevMode;
	}

	public String getPreviewUrl() {
		return previewUrl;
	}

	public int getVersion() {
		return version;
	}

	//	public ReportMetaParamCommon getReportMetaParamCommon() {
	//		return reportMetaParamCommon;
	//	}

	//	public List<ReportMetaParamSpecial> getReportMetaParamSpecialList() {
	//		return ObjectFilters.disabledFilter(reportMetaParamSpecialList);
	//	}

	public Integer getReportTypeOrder() {
		return reportTypeOrder;
	}

	@Override
	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public String getReportSystem() {
		return reportSystem;
	}

	public String getReportCategory() {
		return reportCategory;
	}

	public String getResourceCategory() {
		return resourceCategory;
	}

	//public List<ReportTypeContServiceType> getContServiceTypes() {
	//	return contServiceTypes;
	//}

}
