package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.data.model.ReportMetaParamCommon;
import ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.model.types.ReportMetaParamSpecialTypeName;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;
import ru.excbt.datafuse.nmk.report.ReportOutputFileType;
import ru.excbt.datafuse.nmk.report.ReportPeriodKey;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;

public class ReportMakerParam {

	private static final Logger logger = LoggerFactory.getLogger(ReportMakerParam.class);

	public final static ReportOutputFileType DEFAULT_OUTPUT_FILE_TYPE = ReportOutputFileType.PDF;
	private final static String EXT_ZIP = ".zip";
	private final static String MIME_ZIP = "application/zip";
	private final static String PAR_IDPARAM = "PAR_ID_PARAM";

	private final SubscriberParam subscriberParam;
	private final Long[] paramContObjectIds;
	private final Long[] subscrContObjectIds;
	private final ReportParamset reportParamset;
	private final boolean previewMode;

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIds
	 */
	protected ReportMakerParam(SubscriberParam subscriberParam, ReportParamset reportParamset,
			Long[] paramContObjectIds, Long[] subscrContObjectIds, boolean previewMode) {
		checkNotNull(subscriberParam);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getReportTemplate());
		checkNotNull(paramContObjectIds);
		checkArgument(paramContObjectIds.length > 0);
		this.subscriberParam = subscriberParam;
		this.reportParamset = reportParamset;
		this.paramContObjectIds = Arrays.copyOf(paramContObjectIds, paramContObjectIds.length);
		this.subscrContObjectIds = subscrContObjectIds != null
				? Arrays.copyOf(subscrContObjectIds, subscrContObjectIds.length) : new Long[0];
		this.previewMode = previewMode;
	}

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIds
	 */
	public ReportMakerParam(SubscriberParam subscriberParam, ReportParamset reportParamset, Long[] paramContObjectIds) {
		this(subscriberParam, reportParamset, paramContObjectIds, null, false);
	}

	/**
	 * 
	 * @param reportParamset
	 * @param paramContObjectIdList
	 */
	public ReportMakerParam(SubscriberParam subscriberParam, ReportParamset reportParamset, List<Long> paramContObjectIdList,
			List<Long> subscrContObjectIdList, boolean previewMode) {
		this(subscriberParam, reportParamset, checkNotNull(paramContObjectIdList).toArray(new Long[0]),
				subscrContObjectIdList != null ? subscrContObjectIdList.toArray(new Long[0]) : null, previewMode);
	}

	/**
	 * 
	 * @param reportParamset
	 * @param paramContObjectIdList
	 */
	public ReportMakerParam(SubscriberParam subscriberParam, ReportParamset reportParamset,
			List<Long> paramContObjectIdList) {
		this(subscriberParam, reportParamset, paramContObjectIdList, null, false);
	}

	/**
	 * 
	 * @return
	 */
	public Long[] getParamContObjectIds() {
		return paramContObjectIds == null ? null : Arrays.copyOf(paramContObjectIds, paramContObjectIds.length);
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
	 * (
	 * 
	 * @return
	 */
	public List<Long> getReportContObjectIds() {
		List<Long> result = new ArrayList<>();
		if (paramContObjectIds != null && paramContObjectIds.length > 0) {
			result.addAll(Arrays.asList(paramContObjectIds));
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

		return subscriberParam.getSubscriberId();// reportParamset.getSubscriberId();
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

	/**
	 * 
	 * @param isRequred
	 * @param obj
	 * @return
	 */
	private boolean checkRequiredParamNotNull(Boolean isRequired, Object obj) {
		if (Boolean.TRUE.equals(isRequired)) {
			return (obj != null);
		}
		return true;
	}

	/**
	 * 
	 * @param typeKey
	 * @param param
	 * @return
	 */
	private boolean checkParamSpecialFieldValue(ReportMetaParamSpecialTypeName typeName,
			ReportParamsetParamSpecial param) {

		boolean result = false;

		switch (typeName) {
		case STRING:
			result = param.getTextValue() != null;
			break;
		case DATE:
			result = param.getOneDateValue() != null;
			break;
		case DATETIME:
			result = param.getOneDateValue() != null;
			break;
		case BOOL:
			result = param.getBoolValue() != null;
			break;
		case NUMERIC:
			result = param.getNumericValue() != null;
			break;
		case DATE_PERIOD:
			result = param.getStartDateValue() != null && param.getEndDateValue() != null;
			break;
		case DATETIME_PERIOD:
			result = param.getStartDateValue() != null && param.getEndDateValue() != null;
			break;
		case DIRECTORY:
			result = param.getDirectoryValue() != null;
			break;
		default:
			break;
		}

		return result;
	}

	public boolean isAllCommonRequiredParamsExists() {

		boolean result = true;

		ReportType reportType = reportParamset.getReportTemplate().getReportType();

		ReportMetaParamCommon paramCommon = reportType.getReportMetaParamCommon();

		if (reportParamset.getReportPeriodKey() == ReportPeriodKey.INTERVAL) {

			result = result && checkRequiredParamNotNull(paramCommon.getStartDateRequired(),
					reportParamset.getParamsetStartDate());

			result = result
					&& checkRequiredParamNotNull(paramCommon.getEndDateRequired(), reportParamset.getParamsetEndDate());
		}

		if (reportParamset.getReportPeriodKey() == ReportPeriodKey.DAY) {
			result = result
					&& checkRequiredParamNotNull(paramCommon.getOneDateRequired(), reportParamset.getParamsetOneDate());
		}

		// Only one object required
		if (Boolean.TRUE.equals(paramCommon.getOneContObjectRequired())
				&& Boolean.FALSE.equals(paramCommon.getManyContObjectsRequired())) {
			result = result && getReportContObjectIds().size() == 1;
		}

		// More than 0 Objects required
		if (Boolean.TRUE.equals(paramCommon.getOneContObjectRequired())
				&& Boolean.TRUE.equals(paramCommon.getManyContObjectsRequired())) {
			result = result && getReportContObjectIds().size() > 0;
		}

		// More than 1 object required
		if (Boolean.FALSE.equals(paramCommon.getOneContObjectRequired())
				&& Boolean.TRUE.equals(paramCommon.getManyContObjectsRequired())) {
			result = result && getReportContObjectIds().size() > 1;
		}

		return result;
	}

	/**
	 * 
	 * @param reportMakerParam
	 * @return
	 */
	public boolean isAllSpecialRequiredParamsExists() {

		boolean result = true;

		ReportType reportType = reportParamset.getReportTemplate().getReportType();

		checkNotNull(reportType.getReportMetaParamSpecialList());

		Collection<ReportMetaParamSpecial> specialParamDefs = reportType.getReportMetaParamSpecialList();

		logger.debug("specialParamDefs. size: {}", specialParamDefs.size());
		logger.debug("paramSpecialList.size: {}", reportParamset.getParamSpecialList().size());

		List<ReportParamsetParamSpecial> paramValues = new ArrayList<>();
		paramValues.addAll(reportParamset.getParamSpecialList());

		for (ReportMetaParamSpecial paramDef : specialParamDefs) {
			logger.debug("Checking paramDef: {}", paramDef.getParamSpecialKeyname());
			if (result && Boolean.TRUE.equals(paramDef.getParamSpecialRequired())) {
				logger.debug("paramDef. id:{} keyname:{} ({}) .... required: {}", paramDef.getId(),
						paramDef.getParamSpecialKeyname(), paramDef.getParamSpecialCaption(),
						paramDef.getParamSpecialRequired());

				boolean checkRequired = false;

				logger.debug("paramValues.size: {}", paramValues.size());

				List<ReportParamsetParamSpecial> currCheckValues = new ArrayList<>();
				currCheckValues.addAll(paramValues);

				for (ReportParamsetParamSpecial checkValue : currCheckValues) {

					if (paramDef.getId().equals(checkValue.getReportMetaParamSpecialId())) {

						// String paramTypeKeyname =
						// paramDef.getParamSpecialType().getKeyname();
						String paramTypeName = paramDef.getParamSpecialType().getSpecialTypeName();

						logger.debug("Found param value id: {}. paramDef.id: {} paramTypeKeyname: {}",
								checkValue.getId(), paramDef.getId(), paramDef.getParamSpecialKeyname());

						if (paramTypeName == null) {
							break;
						}
						ReportMetaParamSpecialTypeName typeName = ReportMetaParamSpecialTypeName.valueOf(paramTypeName);

						checkRequired = checkParamSpecialFieldValue(typeName, checkValue);

						// logger.debug("Remove: {}",
						// paramValues.remove(paramValue));
						paramValues.remove(checkValue);
						// break;

					}
				}

				result = result && checkRequired;
			}
		}

		return result;
	}

	public SubscriberParam getSubscriberParam() {
		return subscriberParam;
	}

}
