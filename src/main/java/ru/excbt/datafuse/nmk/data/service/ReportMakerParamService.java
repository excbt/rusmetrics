package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetParamSpecial;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.data.model.vo.ReportTypeWithParamsVO;
import ru.excbt.datafuse.nmk.data.repository.ReportParamsetRepository;
import ru.excbt.datafuse.nmk.data.repository.ReportParamsetUnitRepository;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
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

	private static final boolean PREVIEW_ON = true;
	private static final boolean PREVIEW_OFF = false;


	private final ReportParamsetRepository reportParamsetRepository;

	private final ReportParamsetUnitRepository reportParamsetUnitRepository;

	private final ReportTypeService reportTypeService;

	private final CurrentSubscriberService currentSubscriberService;

	private final ObjectAccessService objectAccessService;

    public ReportMakerParamService(ReportParamsetRepository reportParamsetRepository, ReportParamsetUnitRepository reportParamsetUnitRepository, ReportTypeService reportTypeService, CurrentSubscriberService currentSubscriberService, ObjectAccessService objectAccessService) {
        this.reportParamsetRepository = reportParamsetRepository;
        this.reportParamsetUnitRepository = reportParamsetUnitRepository;
        this.reportTypeService = reportTypeService;
        this.currentSubscriberService = currentSubscriberService;
        this.objectAccessService = objectAccessService;
    }

    /**
	 *
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportMakerParam newReportMakerParam(long reportParamsetId) {
		ReportParamset reportParamset = reportParamsetRepository.findOne(reportParamsetId);

		return reportMakerParamFactory(reportParamset, null, PREVIEW_OFF);

	}

	/**
	 *
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportMakerParam newReportMakerParamPreview(long reportParamsetId) {
		ReportParamset reportParamset = reportParamsetRepository.findOne(reportParamsetId);

		return reportMakerParamFactory(reportParamset, null, PREVIEW_ON);

	}

	/**
	 *
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportMakerParam newReportMakerParam(long reportParamsetId, Long[] contObjectIds) {
		ReportParamset reportParamset = reportParamsetRepository.findOne(reportParamsetId);
		return reportMakerParamFactory(reportParamset, contObjectIds, PREVIEW_OFF);
	}

	/**
	 *
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportMakerParam newReportMakerParamPreview(long reportParamsetId, Long[] contObjectIds) {
		ReportParamset reportParamset = reportParamsetRepository.findOne(reportParamsetId);
		return reportMakerParamFactory(reportParamset, contObjectIds, PREVIEW_ON);
	}

	/**
	 *
	 * @param reportParamset
	 * @param contObjectIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportMakerParam newReportMakerParam(ReportParamset reportParamset, Long[] contObjectIds) {
		return reportMakerParamFactory(reportParamset, contObjectIds, PREVIEW_OFF);
	}

    /**
     *
     * @param reportParamset
     * @return
     */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportMakerParam newReportMakerParam(ReportParamset reportParamset) {
		return reportMakerParamFactory(reportParamset, null, PREVIEW_OFF);
	}

	/**
	 *
	 * @param reportParamset
	 * @param contObjectIds
	 * @return
	 */
	//	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	//	public ReportMakerParam newReportMakerParam(ReportParamset reportParamset, Long[] contObjectIds,
	//			boolean previewMode) {
	//
	//		return reportMakerParamFactory(reportParamset, contObjectIds, previewMode);
	//
	//	}

	/**
	 *
	 * @param reportParamset
	 * @param contObjectIds
	 * @param previewMode
	 * @return
	 */
	private ReportMakerParam reportMakerParamFactory(ReportParamset reportParamset, Long[] contObjectIds,
			boolean previewMode) {

		checkNotNull(reportParamset);

		List<Long> paramContObjectIdList = Collections.emptyList();

		if (contObjectIds != null && contObjectIds.length > 0) {
			paramContObjectIdList = Arrays.asList(contObjectIds);
		} else if (contObjectIds == null && reportParamset.getId() != null) {
			paramContObjectIdList = reportParamsetUnitRepository.selectObjectIds(reportParamset.getId());
		}

		List<Long> subscrContObjectIds = null;

		SubscriberParam subscriberParam = currentSubscriberService.getSubscriberParam();

		String reportTypeKeyname = reportParamset.getReportTemplate().getReportTypeKeyname();
		ReportType reportType = reportTypeService.selectReportType(reportTypeKeyname);

		ReportTypeWithParamsVO reportTypeWithParamsVO = reportTypeService.makeReportTypeParam(reportType);

		if (paramContObjectIdList.isEmpty()) {

			if (!Boolean.TRUE.equals(reportTypeWithParamsVO.getReportMetaParamCommon()
					.getNoContObjectsRequired())) {

				//				Long subscriberId = reportParamset.getSubscriber() != null ? reportParamset.getSubscriber().getId()
				//						: reportParamset.getSubscriberId();
				Long subscriberId = subscriberParam.getSubscriberId();

				subscrContObjectIds = objectAccessService.findContObjectIds(subscriberId);
			}

		}

		return new ReportMakerParam(subscriberParam, reportTypeWithParamsVO, reportParamset,
				paramContObjectIdList, subscrContObjectIds, previewMode);

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

		String reportTypeKeyname = reportMakerParam.getReportParamset().getReportTemplate().getReportTypeKeyname();
		ReportType reportType = reportTypeService.findByKeyname(reportTypeKeyname);

		if (reportType == null) {
			reportType = reportTypeService.findByKeyname(reportTypeKey);
		}

		Map<Long, ReportMetaParamSpecial> metaParamMap = new HashMap<>();

		for (ReportMetaParamSpecial metaParam : reportMakerParam.getReportType().getReportMetaParamSpecialList()) {
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
