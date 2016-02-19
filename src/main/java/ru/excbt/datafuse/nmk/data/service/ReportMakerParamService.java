package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ReportMetaParamCommon;
import ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetParamSpecial;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.data.model.types.ReportMetaParamSpecialTypeName;
import ru.excbt.datafuse.nmk.report.ReportPeriodKey;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;

/**
 * Сервис для работы с параметрами отчета
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.06.2015
 *
 */
@Service
public class ReportMakerParamService {

	private static final Logger logger = LoggerFactory.getLogger(ReportMakerParamService.class);

	@Autowired
	private ReportParamsetService reportParamsetService;

	// @Autowired
	// private SubscriberService subscriberService;

	@Autowired
	private ReportTypeService reportTypeService;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportMakerParam newReportMakerParam(long reportParamsetId) {
		ReportParamset reportParamset = reportParamsetService.findOne(reportParamsetId);

		return newReportMakerParam(reportParamset, null, false);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportMakerParam newReportMakerParam(long reportParamsetId, boolean previewMode) {
		ReportParamset reportParamset = reportParamsetService.findOne(reportParamsetId);

		return newReportMakerParam(reportParamset, null, previewMode);

	}

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportMakerParam newReportMakerParam(long reportParamsetId, Long[] contObjectIds) {
		ReportParamset reportParamset = reportParamsetService.findOne(reportParamsetId);
		return newReportMakerParam(reportParamset, contObjectIds, false);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportMakerParam newReportMakerParam(long reportParamsetId, Long[] contObjectIds, boolean previewMode) {
		ReportParamset reportParamset = reportParamsetService.findOne(reportParamsetId);
		return newReportMakerParam(reportParamset, contObjectIds, previewMode);
	}

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportMakerParam newReportMakerParam(ReportParamset reportParamset, Long[] contObjectIds) {
		return newReportMakerParam(reportParamset, contObjectIds, false);
	}

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportMakerParam newReportMakerParam(ReportParamset reportParamset) {
		return newReportMakerParam(reportParamset, null, false);
	}

	/**
	 * 
	 * @param reportParamset
	 * @param contObjectIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportMakerParam newReportMakerParam(ReportParamset reportParamset, Long[] contObjectIds,
			boolean previewMode) {

		checkNotNull(reportParamset);

		List<Long> resultContObjectIdList = Collections.emptyList();

		if (contObjectIds != null && contObjectIds.length > 0) {
			resultContObjectIdList = Arrays.asList(contObjectIds);
		} else if (contObjectIds == null && reportParamset.getId() != null) {
			resultContObjectIdList = reportParamsetService.selectReportParamsetObjectIds(reportParamset.getId());
		}

		List<Long> subscrContObjectIds = null;

		if (resultContObjectIdList.isEmpty()) {

			if (!Boolean.TRUE.equals(reportParamset.getReportTemplate().getReportType().getReportMetaParamCommon()
					.getNoContObjectsRequired())) {

				Long subscriberId = reportParamset.getSubscriber() != null ? reportParamset.getSubscriber().getId()
						: reportParamset.getSubscriberId();

				subscrContObjectIds = subscrContObjectService.selectSubscriberContObjectIds(subscriberId);
			}

		}

		return new ReportMakerParam(reportParamset, resultContObjectIdList, subscrContObjectIds, previewMode);

	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean isAllCommonRequiredParamsExists(ReportMakerParam reportMakerParam) {

		checkNotNull(reportMakerParam);
		// checkState(reportMakerParam.isParamsetValid());

		boolean result = true;

		ReportParamset reportParamset = reportMakerParam.getReportParamset();

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
			result = result && reportMakerParam.getReportContObjectIds().size() == 1;
		}

		// More than 0 Objects required
		if (Boolean.TRUE.equals(paramCommon.getOneContObjectRequired())
				&& Boolean.TRUE.equals(paramCommon.getManyContObjectsRequired())) {
			result = result && reportMakerParam.getReportContObjectIds().size() > 0;
		}

		// More than 1 object required
		if (Boolean.FALSE.equals(paramCommon.getOneContObjectRequired())
				&& Boolean.TRUE.equals(paramCommon.getManyContObjectsRequired())) {
			result = result && reportMakerParam.getReportContObjectIds().size() > 1;
		}

		return result;
	}

	/**
	 * 
	 * @param reportMakerParam
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean isAllSpecialRequiredParamsExists(ReportMakerParam reportMakerParam) {

		checkNotNull(reportMakerParam);
		// checkState(reportMakerParam.isParamsetValid());

		boolean result = true;

		ReportParamset reportParamset = reportMakerParam.getReportParamset();

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

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Map<String, Object> getParamSpecialValues(ReportMakerParam reportMakerParam) {

		checkNotNull(reportMakerParam);

		Map<String, Object> result = new HashMap<String, Object>();

		String keyname = reportMakerParam.getReportParamset().getReportTemplate().getReportTypeKeyname();

		ReportTypeKey reportTypeKey = ReportTypeKey.valueOf(keyname);

		checkNotNull(reportTypeKey);

		logger.debug("Checking params of reportType:{} ", reportTypeKey);

		ReportType reportType = reportMakerParam.getReportParamset().getReportTemplate().getReportType();

		if (reportType == null) {
			reportType = reportTypeService.findByKeyname(reportTypeKey);
		}

		Map<Long, ReportMetaParamSpecial> metaParamMap = new HashMap<>();

		for (ReportMetaParamSpecial metaParam : reportType.getReportMetaParamSpecialList()) {
			metaParamMap.put(metaParam.getId(), metaParam);
		}

		Iterable<ReportParamsetParamSpecial> paramSpecials = reportMakerParam.getReportParamset().getParamSpecialList();

		for (ReportParamsetParamSpecial paramV : paramSpecials) {

			ReportMetaParamSpecial metaParamSpecial = metaParamMap.get(paramV.getReportMetaParamSpecialId());

			if (metaParamSpecial == null) {
				continue;
			}

			checkNotNull(metaParamSpecial,
					"MetaParamSpecial with id:" + paramV.getReportMetaParamSpecialId() + " is not found");

			if (!paramV.isAnyValueAssigned()) {
				if (Boolean.TRUE.equals(metaParamSpecial.getParamSpecialRequired())) {
					throw new IllegalStateException("Value of required metaParamSpecial id:" + metaParamSpecial.getId()
							+ ", keyname:" + metaParamSpecial.getParamSpecialKeyname() + " is null");
				}
				continue;
			}

			logger.debug("paramSpecial id:{}, metaParamSpecialId:{}, metaKeyname:{}, paramValue:{}", paramV.getId(),
					metaParamSpecial.getId(), metaParamSpecial.getParamSpecialKeyname(), paramV.getValuesAsString());

			Map<String, Object> valueMap = paramV.getValuesAsMap();

			if (metaParamSpecial.getParamSpecialType().getSpecialTypeField1() != null
					&& metaParamSpecial.getParamSpecialName1() != null) {
				String srcKey = metaParamSpecial.getParamSpecialType().getSpecialTypeField1();
				String dstKey = metaParamSpecial.getParamSpecialName1();
				Object value = valueMap.get(srcKey);

				checkNotNull(value, "value with key:" + srcKey + " is null");

				Object checkValue = result.put(dstKey, value);
				checkState(checkValue == null, "Param with key:" + dstKey + " is already added");
			}

			if (metaParamSpecial.getParamSpecialType().getSpecialTypeField2() != null
					&& metaParamSpecial.getParamSpecialName2() != null) {
				String srcKey = metaParamSpecial.getParamSpecialType().getSpecialTypeField2();
				String dstKey = metaParamSpecial.getParamSpecialName2();
				Object value = valueMap.get(srcKey);
				checkNotNull(value, "value with key:" + srcKey + " is null");
				Object checkValue = result.put(dstKey, value);

				checkState(checkValue == null, "Param with key:" + dstKey + " is already added");
			}

		}

		return result;
	}
}
