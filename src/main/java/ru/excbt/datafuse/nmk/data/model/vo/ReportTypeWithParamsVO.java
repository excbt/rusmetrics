package ru.excbt.datafuse.nmk.data.model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import ru.excbt.datafuse.nmk.data.model.ReportMetaParamCommon;
import ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial;
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

	public ReportTypeWithParamsVO(ReportType reportType, ReportMetaParamCommon reportMetaParamCommon,
			List<ReportMetaParamSpecial> reportMetaParamSpecialList) {
		this.reportType = reportType;
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

}
