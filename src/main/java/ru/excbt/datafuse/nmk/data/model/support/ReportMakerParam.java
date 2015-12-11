package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.report.ReportOutputFileType;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;

public class ReportMakerParam {

	private static final Logger logger = LoggerFactory.getLogger(ReportMakerParam.class);

	public final static ReportOutputFileType DEFAULT_OUTPUT_FILE_TYPE = ReportOutputFileType.PDF;
	private final static String EXT_ZIP = ".zip";
	private final static String MIME_ZIP = "application/zip";
	private final static String PAR_IDPARAM = "PAR_ID_PARAM";

	private final Long[] contObjectIds;
	private final Long[] subscrContObjectIds;
	private final ReportParamset reportParamset;
	private final boolean previewMode;

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIds
	 */
	public ReportMakerParam(ReportParamset reportParamset, Long[] contObjectIds, boolean previewMode) {
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getReportTemplate());
		checkNotNull(contObjectIds);
		checkArgument(contObjectIds.length > 0);
		this.reportParamset = reportParamset;
		this.contObjectIds = Arrays.copyOf(contObjectIds, contObjectIds.length);
		this.subscrContObjectIds = Arrays.copyOf(contObjectIds, contObjectIds.length);
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
	public ReportMakerParam(ReportParamset reportParamset, List<Long> contObjectIdList, List<Long> subscrObjectIdList,
			boolean previewMode) {
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getReportTemplate());
		checkNotNull(contObjectIdList);
		// checkArgument(!contObjectIdList.isEmpty());
		this.reportParamset = reportParamset;
		this.contObjectIds = contObjectIdList.toArray(new Long[0]);
		this.subscrContObjectIds = subscrObjectIdList != null ? subscrObjectIdList.toArray(new Long[0]) : new Long[0];
		this.previewMode = previewMode;
	}

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIdList
	 */
	public ReportMakerParam(ReportParamset reportParamset, List<Long> contObjectIdList) {
		this(reportParamset, contObjectIdList, null, false);
	}

	/**
	 * 
	 * @return
	 */
	public Long[] getContObjectIds() {
		return contObjectIds == null ? null : Arrays.copyOf(contObjectIds, contObjectIds.length);
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
	public Long[] getSubscrContObjectIds() {
		return subscrContObjectIds == null ? null : Arrays.copyOf(subscrContObjectIds, subscrContObjectIds.length);
	}

	/**
	 * 
	 * @return
	 */
	public List<Long> getSubscrContObjectList() {
		if (subscrContObjectIds == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(subscrContObjectIds);
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
	 * (
	 * 
	 * @return
	 */
	public List<Long> getReportContObjectIds() {
		List<Long> result = new ArrayList<>();
		if (contObjectIds != null && contObjectIds.length > 0) {
			result.addAll(Arrays.asList(contObjectIds));
		} else {
			if (subscrContObjectIds != null) {
				result.addAll(Arrays.asList(subscrContObjectIds));
			}
		}
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isParamsetValid() {

		return reportParamset.getSubscriber() != null
				// && !reportParamset.isNew()
				&& reportParamset.getReportPeriodKey() != null && reportParamset.getReportTemplate() != null
				&& reportParamset.getReportTemplate().getReportTypeKeyname() != null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSubscriberValid() {
		return reportParamset.getSubscriber() != null && reportParamset.getSubscriberId() != null;
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
				|| (getReportContObjectIds().size() > 1 && Boolean.TRUE.equals(reportParamset.getReportTemplate()
						.getReportType().getReportMetaParamCommon().getManyContObjectsZipOnly()));
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

	/**
	 * 
	 * @return
	 */
	public boolean isSpecialIdParam() {
		boolean result = false;

		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getReportTemplate());

		result = Boolean.TRUE
				.equals(getParamserReportTemplate().getReportType().getReportMetaParamCommon().getIsSpecialIdParam());

		return result;
	}

	/**
	 * 
	 * @return
	 */
	public Long getIdParam() {
		if (isSpecialIdParam()) {
			Object idParam = getReportSpecialParamValue(PAR_IDPARAM);
			if (idParam == null) {
				throw new IllegalStateException("Special idParam is null");
			}
			if (idParam instanceof String) {
				return Long.valueOf((String) idParam);
			}

			if (idParam instanceof BigDecimal) {
				return ((BigDecimal) idParam).longValue();
			}

			throw new IllegalStateException("Special idParam is not of type String or BigDecimal");
		}

		return reportParamset.getSubscriberId();
	}

	/**
	 * 
	 * @return
	 */
	private ReportTemplate getParamserReportTemplate() {
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getReportTemplate());
		return reportParamset.getReportTemplate();
	}

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	private Object getReportSpecialParamValue(String keyname) {
		logger.debug("getReportSpecialParamValue with keyname:{}", keyname);
		checkNotNull(keyname);
		Optional<ReportMetaParamSpecial> optMetaParam = getParamserReportTemplate().getReportType()
				.getReportMetaParamSpecialList().stream().filter((i) -> i.getParamSpecialKeyname().equals(keyname))
				.findFirst();

		if (!optMetaParam.isPresent()) {
			logger.error("Optional ReportMetaParamSpecial is not found");
			return null;
		}

		Long paramId = optMetaParam.get().getId();

		Optional<ReportParamsetParamSpecial> optParamsetParam = getReportParamset().getParamSpecialList().stream()
				.filter((i) -> i.getReportMetaParamSpecialId().equals(paramId)).findFirst();

		if (!optParamsetParam.isPresent()) {
			logger.error("Optional ReportParamsetParamSpecial is not found");
			return null;
		}

		if (!optParamsetParam.get().isAnyValueAssigned()) {
			return null;
		}

		if (!optParamsetParam.get().isOneValueAssigned()) {
			throw new IllegalStateException("Too many values for SpecialParam:" + keyname);
		}

		Map<String, Object> paramValues = optParamsetParam.get().getValuesAsMap();

		Object[] preResult = paramValues.values().toArray();
		if (preResult.length == 1) {
			return preResult[0];
		}

		throw new UnsupportedOperationException("Many Values get is not supported");

	}

	/**
	 * 
	 * @return
	 */
	public ReportTypeKey getReportTypeKey() {
		ReportTemplate rt = getParamserReportTemplate();
		String key = rt.getReportTypeKeyname();
		return rt != null ? ReportTypeKey.valueOf(key) : null;
	}

}
