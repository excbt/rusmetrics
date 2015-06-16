package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ReportMetaParamCommon;
import ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetParamSpecial;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.data.model.support.ReportMetaParamSpecialTypeKey;

@Service
@Transactional(readOnly = true)
public class ReportMakerParamService {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportMakerParamService.class);

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	public ReportMakerParam getReportMakerParam(long reportParamsetId) {
		ReportParamset reportParamset = reportParamsetService
				.findOne(reportParamsetId);

		return getReportMakerParam(reportParamset, null, false);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	public ReportMakerParam getReportMakerParam(long reportParamsetId,
			boolean previewMode) {
		ReportParamset reportParamset = reportParamsetService
				.findOne(reportParamsetId);

		return getReportMakerParam(reportParamset, null, previewMode);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	public ReportMakerParam getReportMakerParam(long reportParamsetId,
			Long[] contObjectIdList) {
		ReportParamset reportParamset = reportParamsetService
				.findOne(reportParamsetId);
		return getReportMakerParam(reportParamset, contObjectIdList, false);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	public ReportMakerParam getReportMakerParam(long reportParamsetId,
			Long[] contObjectIds, boolean previewMode) {
		ReportParamset reportParamset = reportParamsetService
				.findOne(reportParamsetId);
		return getReportMakerParam(reportParamset, contObjectIds, previewMode);
	}

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIds
	 * @return
	 */
	public ReportMakerParam getReportMakerParam(ReportParamset reportParamset,
			Long[] contObjectIds) {
		return getReportMakerParam(reportParamset, contObjectIds, false);
	}

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIds
	 * @return
	 */
	public ReportMakerParam getReportMakerParam(ReportParamset reportParamset) {
		return getReportMakerParam(reportParamset, null, false);
	}

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIds
	 * @return
	 */
	public ReportMakerParam getReportMakerParam(ReportParamset reportParamset,
			Long[] contObjectIds, boolean previewMode) {
		checkNotNull(reportParamset);

		if (contObjectIds != null && contObjectIds.length > 0) {
			return new ReportMakerParam(reportParamset, contObjectIds);
		}

		List<Long> resultContObjectIdList = Collections.emptyList();

		if (contObjectIds == null) {
			resultContObjectIdList = reportParamsetService
					.selectParamsetContObjectIds(reportParamset.getId());
		}

		if (resultContObjectIdList.isEmpty()) {
			resultContObjectIdList = subscriberService
					.selectSubscriberContObjectIds(reportParamset
							.getSubscriberId());
		}

		return new ReportMakerParam(reportParamset, resultContObjectIdList,
				previewMode);

	}

	/**
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public boolean isAllCommonRequiredParamsExists(
			ReportMakerParam reportMakerParam) {

		checkNotNull(reportMakerParam);
		checkState(reportMakerParam.isParamsetValid());

		boolean result = true;

		ReportParamset reportParamset = reportMakerParam.getReportParamset();

		ReportType reportType = reportParamset.getReportTemplate()
				.getReportType();

		ReportMetaParamCommon paramCommon = reportType
				.getReportMetaParamCommon();

		result = result
				&& checkRequiredParamNotNull(paramCommon.getOneDateRequired(),
						reportParamset.getParamsetOneDate());
		result = result
				&& checkRequiredParamNotNull(
						paramCommon.getStartDateRequired(),
						reportParamset.getParamsetStartDate());
		result = result
				&& checkRequiredParamNotNull(paramCommon.getEndDateRequired(),
						reportParamset.getParamsetEndDate());

		// Only one object required
		if (Boolean.TRUE.equals(paramCommon.getOneContObjectRequired())
				&& Boolean.FALSE.equals(paramCommon
						.getManyContObjectsRequired())) {
			result = result && reportMakerParam.getContObjectList().size() == 1;
		}

		// More than 0 Objects required
		if (Boolean.TRUE.equals(paramCommon.getOneContObjectRequired())
				&& Boolean.TRUE
						.equals(paramCommon.getManyContObjectsRequired())) {
			result = result && reportMakerParam.getContObjectList().size() > 0;
		}

		// More than 1 object required
		if (Boolean.FALSE.equals(paramCommon.getOneContObjectRequired())
				&& Boolean.TRUE
						.equals(paramCommon.getManyContObjectsRequired())) {
			result = result && reportMakerParam.getContObjectList().size() > 1;
		}

		return result;
	}

	/**
	 * 
	 * @param reportMakerParam
	 * @return
	 */
	@Transactional(readOnly = true)
	public boolean isAllSpecialRequiredParamsExists(
			ReportMakerParam reportMakerParam) {

		checkNotNull(reportMakerParam);
		checkState(reportMakerParam.isParamsetValid());

		boolean result = true;

		ReportParamset reportParamset = reportMakerParam.getReportParamset();

		ReportType reportType = reportParamset.getReportTemplate()
				.getReportType();

		checkNotNull(reportType.getReportMetaParamSpecialList());

		Collection<ReportMetaParamSpecial> specialParamDefs = reportType
				.getReportMetaParamSpecialList();

		logger.debug("specialParamDefs. size: {}", specialParamDefs.size());
		logger.debug("paramSpecialList.size: {}", reportParamset
				.getParamSpecialList().size());

		List<ReportParamsetParamSpecial> paramValues = new ArrayList<>();
		paramValues.addAll(reportParamset.getParamSpecialList());

		for (ReportMetaParamSpecial paramDef : specialParamDefs) {
			logger.debug("Checking paramDef: {}",
					paramDef.getParamSpecialKeyname());
			if (result
					&& Boolean.TRUE.equals(paramDef.getParamSpecialRequired())) {
				logger.debug(
						"paramDef. id:{} keyname:{} ({}) .... required: {}",
						paramDef.getId(), paramDef.getParamSpecialKeyname(),
						paramDef.getParamSpecialCaption(),
						paramDef.getParamSpecialRequired());

				boolean checkRequired = false;

				logger.debug("paramValues.size: {}", paramValues.size());

				List<ReportParamsetParamSpecial> currCheckValues = new ArrayList<>();
				currCheckValues.addAll(paramValues);

				for (ReportParamsetParamSpecial checkValue : currCheckValues) {

					if (paramDef.getId().equals(
							checkValue.getReportMetaParamSpecialId())) {

						String paramTypeKeyname = paramDef
								.getParamSpecialType().getKeyname();

						logger.debug(
								"Found param value id: {}. paramDef.id: {} paramTypeKeyname: {}",
								checkValue.getId(), paramDef.getId(),
								paramDef.getParamSpecialKeyname());

						if (paramTypeKeyname == null) {
							break;
						}
						ReportMetaParamSpecialTypeKey typeKey = ReportMetaParamSpecialTypeKey
								.valueOf(paramTypeKeyname);

						checkRequired = checkParamSpecialFieldValue(typeKey,
								checkValue);

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
	private boolean checkParamSpecialFieldValue(
			ReportMetaParamSpecialTypeKey typeKey,
			ReportParamsetParamSpecial param) {

		boolean result = false;

		switch (typeKey) {
		case SPECIAL_STRING:
			result = param.getTextValue() != null;
			break;
		case SPECIAL_DATE:
			result = param.getOneDateValue() != null;
			break;
		case SPECIAL_DATETIME:
			result = param.getOneDateValue() != null;
			break;
		case SPECIAL_BOOL:
			result = param.getBoolValue() != null;
			break;
		case SPECIAL_NUMERIC:
			result = param.getNumericValue() != null;
			break;
		case SPECIAL_PERIOD_DATE:
			result = param.getStartDateValue() != null
					&& param.getEndDateValue() != null;
			break;
		case SPECIAL_PERIOD_DATETIME:
			result = param.getStartDateValue() != null
					&& param.getEndDateValue() != null;
			break;
		case SPECIAL_CONT_OBJECT_GROUP:
			result = param.getDirectoryValue() != null;
			break;
		default:
			break;
		}

		return result;
	}
}
