package ru.excbt.datafuse.nmk.data.model.vo;

import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

public class ReportParamsetVO implements DeletedMarker {

	public static final Comparator<ReportParamsetVO> COMPARATOR = new ReportParamsetVOComparator();

	private static class ReportParamsetVOComparator implements Comparator<ReportParamsetVO> {

		@Override
		public int compare(ReportParamsetVO o1, ReportParamsetVO o2) {
			return o1.reportTypeOrder - o2.reportTypeOrder;
		}
	}

	@JsonUnwrapped
	private final ReportParamset reportParamset;

	private int reportTypeOrder;

	public ReportParamsetVO(ReportParamset reportParamset) {

		this.reportParamset = reportParamset;
	}

	public Integer getReportTypeOrder() {
		return reportTypeOrder;
	}

	public void setReportTypeOrder(Integer reportTypeOrder) {
		this.reportTypeOrder = reportTypeOrder != null ? reportTypeOrder : 0;
	}

	public ReportParamset getReportParamset() {
		return reportParamset;
	}

	@Override
	public int getDeleted() {
		return reportParamset != null ? reportParamset.getDeleted() : 1;
	}

}
