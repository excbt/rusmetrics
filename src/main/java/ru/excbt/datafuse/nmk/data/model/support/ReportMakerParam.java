package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

import ru.excbt.datafuse.nmk.data.model.ReportParamset;

public class ReportMakerParam {

	private final Long[] contObjectIds;
	private final ReportParamset reportParamset;

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIds
	 */
	public ReportMakerParam(ReportParamset reportParamset, Long[] contObjectIds) {
		checkNotNull(reportParamset);
		checkNotNull(contObjectIds);
		checkArgument(contObjectIds.length > 0);
		this.reportParamset = reportParamset;
		this.contObjectIds = Arrays.copyOf(contObjectIds, contObjectIds.length);
	}

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIds
	 */
	public ReportMakerParam(ReportParamset reportParamset,
			List<Long> contObjectIds) {
		checkNotNull(reportParamset);
		checkNotNull(contObjectIds);
		checkArgument(!contObjectIds.isEmpty());
		this.reportParamset = reportParamset;
		this.contObjectIds = contObjectIds.toArray(new Long[0]);
	}

	/**
	 * 
	 * @return
	 */
	public Long[] getContObjectIds() {
		return contObjectIds;
	}

	/**
	 * 
	 * @return
	 */
	public List<Long> getContObjectList() {
		return Arrays.asList(contObjectIds);
	}

	/**
	 * 
	 * @return
	 */
	public ReportParamset getReportParamset() {
		return reportParamset;
	}

	/**
	 * 
	 * @return
	 */
	public boolean contObjectsEmpty() {
		return contObjectIds == null || contObjectIds.length == 0;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isParamsetValid() {
		return reportParamset.getSubscriber() != null
				&& !reportParamset.isNew()
				&& reportParamset.getReportPeriodKey() != null
				&& reportParamset.getReportTemplate() != null
				&& reportParamset.getReportTemplate().getReportTypeKey() != null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSubscriberValid() {
		return reportParamset.getSubscriber() != null
				&& reportParamset.getSubscriberId() != null;
	}
}
