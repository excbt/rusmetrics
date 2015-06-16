package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportOutputFileType;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;

public class ReportMakerParam {

	public final static ReportOutputFileType DEFAULT_OUTPUT_FILE_TYPE = ReportOutputFileType.PDF;
	private final static String EXT_ZIP = ".zip";
	private final static String MIME_ZIP = "application/zip";

	private final Long[] contObjectIds;
	private final ReportParamset reportParamset;
	private final boolean previewMode;

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIds
	 */
	public ReportMakerParam(ReportParamset reportParamset,
			Long[] contObjectIds, boolean previewMode) {
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getReportTemplate());
		checkNotNull(contObjectIds);
		checkArgument(contObjectIds.length > 0);
		this.reportParamset = reportParamset;
		this.contObjectIds = Arrays.copyOf(contObjectIds, contObjectIds.length);
		this.previewMode = previewMode;
	}

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIds
	 */
	public ReportMakerParam(ReportParamset reportParamset, Long[] contObjectIds) {
		this(reportParamset, contObjectIds, false);
	}

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIdList
	 */
	public ReportMakerParam(ReportParamset reportParamset,
			List<Long> contObjectIdList, boolean previewMode) {
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getReportTemplate());
		checkNotNull(contObjectIdList);
		checkArgument(!contObjectIdList.isEmpty());
		this.reportParamset = reportParamset;
		this.contObjectIds = contObjectIdList.toArray(new Long[0]);
		this.previewMode = previewMode;
	}

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIdList
	 */
	public ReportMakerParam(ReportParamset reportParamset,
			List<Long> contObjectIdList) {
		this(reportParamset, contObjectIdList, false);
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
		if (contObjectIds == null) {
			return Collections.emptyList();
		}
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
				//&& !reportParamset.isNew()
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

	/**
	 * 
	 * @return
	 */
	public boolean isOutputFileZipped() {

		if (previewMode) {
			return false;
		}

		return Boolean.TRUE.equals(reportParamset.getOutputFileZipped())
				|| (getContObjectList().size() > 1 && Boolean.TRUE
						.equals(reportParamset.getReportTemplate()
								.getReportType().getReportMetaParamCommon()
								.getManyContObjectsZipOnly()));
	}

	/**
	 * 
	 * @return
	 */
	public String getMimeType() {
		if (isOutputFileZipped()) {
			return MIME_ZIP;
		}
		return reportOutputFileType().getMimeType();
	}

	public String getExt() {

		if (isOutputFileZipped()) {
			return EXT_ZIP;
		}

		return reportOutputFileType().getExt();
	}

	/**
	 * 
	 * @return
	 */
	public ReportOutputFileType reportOutputFileType() {

		if (previewMode) {
			return ReportOutputFileType.HTML;
		}

		return reportParamset.getOutputFileType() == null ? DEFAULT_OUTPUT_FILE_TYPE
				: reportParamset.getOutputFileType();
	}

	public boolean isPreviewMode() {
		return previewMode;
	}
}
