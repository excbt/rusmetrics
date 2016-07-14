package ru.excbt.datafuse.nmk.data.model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import ru.excbt.datafuse.nmk.data.model.ReportMetaParamCommon;
import ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportTypeContServiceType;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;

public class ReportTypeWithParamsVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3345774922530110217L;

	@JsonUnwrapped
	private final ReportType reportType;

	private final ReportMetaParamCommon reportMetaParamCommon;

	private final List<ReportMetaParamSpecial> reportMetaParamSpecialList;

	private final List<ReportTypeContServiceType> contServiceTypes;

	public ReportTypeWithParamsVO(ReportType reportType, List<ReportTypeContServiceType> contServiceTypes,
			ReportMetaParamCommon reportMetaParamCommon,
			List<ReportMetaParamSpecial> reportMetaParamSpecialList) {
		this.reportType = reportType;
		this.contServiceTypes = contServiceTypes != null
				? new ArrayList<>(contServiceTypes) : new ArrayList<>();
		this.reportMetaParamCommon = reportMetaParamCommon;
		this.reportMetaParamSpecialList = reportMetaParamSpecialList != null
				? new ArrayList<>(reportMetaParamSpecialList) : new ArrayList<>();
	}

	public ReportMetaParamCommon getReportMetaParamCommon() {
		return reportMetaParamCommon;
	}

	public List<ReportMetaParamSpecial> getReportMetaParamSpecialList() {
		return reportMetaParamSpecialList;
	}

	public ReportType getReportType() {
		return reportType;
	}

	public List<ReportTypeContServiceType> getContServiceTypes() {
		return Collections.unmodifiableList(contServiceTypes);
	}

}
