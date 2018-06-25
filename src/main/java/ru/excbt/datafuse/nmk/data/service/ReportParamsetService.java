package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ReportMetaParamDirectoryItem;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetUnit;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.data.model.vo.ReportParamsetVO;
import ru.excbt.datafuse.nmk.data.repository.*;
import ru.excbt.datafuse.nmk.report.ReportOutputFileType;
import ru.excbt.datafuse.nmk.report.ReportPeriodKey;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.SubscriberService;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

/**
 * Сервис для работы с набором параметров отчета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 14.04.2015
 *
 */
@Service
public class ReportParamsetService implements SecuredRoles {

	private static final Logger log = LoggerFactory.getLogger(ReportParamsetService.class);

	private static final String REPORT_AUTO_SUFFIX = "REPORT_AUTO_SUFFIX";

	private final ReportParamsetRepository reportParamsetRepository;

	private final ReportParamsetUnitRepository reportParamsetUnitRepository;

	private final ReportParamsetUnitFilterRepository reportParamsetUnitFilterRepository;

	private final ReportTemplateRepository reportTemplateRepository;

	private final SubscriberService subscriberService;

	private final ReportSheduleService reportSheduleService;

	private final ReportMakerParamService reportMakerParamService;

	private final SystemParamService systemParamService;

	private final ReportTypeService reportTypeService;

	private final ReportMetaParamDirectoryItemRepository reportMetaParamDirectoryItemRepository;

	private final ContObjectMapper contObjectMapper;

    public ReportParamsetService(ReportParamsetRepository reportParamsetRepository, ReportParamsetUnitRepository reportParamsetUnitRepository, ReportParamsetUnitFilterRepository reportParamsetUnitFilterRepository, ReportTemplateRepository reportTemplateRepository, SubscriberService subscriberService, ReportSheduleService reportSheduleService, ReportMakerParamService reportMakerParamService, SystemParamService systemParamService, ReportTypeService reportTypeService, ReportMetaParamDirectoryItemRepository reportMetaParamDirectoryItemRepository, ContObjectMapper contObjectMapper) {
        this.reportParamsetRepository = reportParamsetRepository;
        this.reportParamsetUnitRepository = reportParamsetUnitRepository;
        this.reportParamsetUnitFilterRepository = reportParamsetUnitFilterRepository;
        this.reportTemplateRepository = reportTemplateRepository;
        this.subscriberService = subscriberService;
        this.reportSheduleService = reportSheduleService;
        this.reportMakerParamService = reportMakerParamService;
        this.systemParamService = systemParamService;
        this.reportTypeService = reportTypeService;
        this.reportMetaParamDirectoryItemRepository = reportMetaParamDirectoryItemRepository;
        this.contObjectMapper = contObjectMapper;
    }

    /**
	 *
	 * @param reportParamset
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public ReportParamset createReportParamset(ReportParamset reportParamset, Long[] contObjectIds) {

		checkNotNull(reportParamset);
		checkArgument(reportParamset.isNew());

		for (ReportParamsetParamSpecial param : reportParamset.getParamSpecialList()) {
			param.setReportParamset(reportParamset);
		}

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamset, contObjectIds);

		boolean requiredPassed = reportMakerParam.isAllCommonRequiredParamsExists()
				&& reportMakerParam.isAllSpecialRequiredParamsExists();

		reportParamset.setAllRequiredParamsPassed(requiredPassed);

		ReportParamset result = reportParamsetRepository.save(reportParamset);

		if (contObjectIds != null) {
			updateUnitToParamset(result.getId(), contObjectIds);
		}
		return result;
	}

	/**
	 *
	 * @param entity
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteReportParamset(ReportParamset entity) {
		checkNotNull(entity);
		if (checkCanUpdate(entity.getId())) {
			reportParamsetRepository.delete(entity);
		} else {
			throw new PersistenceException(String.format("Can't delete ReportParamset(id=%d)", entity.getId()));
		}

	}

    /**
     *
     * @param reportParamset
     * @return
     */
	protected ReportParamset updateReportParamset(ReportParamset reportParamset) {
		checkNotNull(reportParamset);
		checkArgument(!reportParamset.isNew());

		for (ReportParamsetParamSpecial param : reportParamset.getParamSpecialList()) {
			param.setReportParamset(reportParamset);
		}

		if (reportParamset.getParamsetStartDate() != null) {
			reportParamset
					.setParamsetStartDate(JodaTimeUtils.startOfDay(reportParamset.getParamsetStartDate()).toDate());
		}

		if (reportParamset.getParamsetEndDate() != null) {
			reportParamset.setParamsetEndDate(JodaTimeUtils.endOfDay(reportParamset.getParamsetEndDate()).toDate());
		}

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamset);

		boolean requiredPassed = reportMakerParam.isAllCommonRequiredParamsExists()
				&& reportMakerParam.isAllSpecialRequiredParamsExists();

		reportParamset.setAllRequiredParamsPassed(requiredPassed);

		ReportParamset result = null;
		if (checkCanUpdate(reportParamset.getId())) {
			result = reportParamsetRepository.save(reportParamset);
		} else {
			throw new PersistenceException(
					String.format("Can't update common Report Paramset (id=%d)", reportParamset.getId()));
		}

		return result;
	}

    /**
     *
     * @param reportParamset
     * @param contObjectIds
     * @return
     */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public ReportParamset updateOne(ReportParamset reportParamset, Long[] contObjectIds) {

		checkNotNull(reportParamset);

		ReportParamset result = updateReportParamset(reportParamset);

		if (contObjectIds != null) {
			updateUnitToParamset(result.getId(), contObjectIds);
		}
		return result;
	}

	/**
	 *
	 * @param id
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteReportParamset(long id) {
		if (checkCanUpdate(id)) {
			reportParamsetUnitRepository.softDeleteByReportParamset(id);
			reportSheduleService.deleteByReportParamset(id);
			reportParamsetRepository.deleteById(id);
		} else {
			throw new PersistenceException(String.format("Can't delete ReportParamset(id=%d)", id));
		}

	}

	/**
	 *
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ReportParamset> findReportParamsetList(long reportTemplateId) {
		return reportParamsetRepository.findByReportTemplateId(reportTemplateId);
	}

	/**
	 *
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ReportParamset> selectReportTypeParamsetList(ReportTypeKey reportType, boolean isActive,
			long subscriberId) {
		List<ReportParamset> commonReportParams = reportParamsetRepository
				.selectCommonReportParamset(reportType.getKeyname(), isActive);
		List<ReportParamset> subscriberReportParams = reportParamsetRepository
				.selectSubscriberReportParamset(reportType.getKeyname(), isActive, subscriberId);

		List<ReportParamset> result = new ArrayList<>();
		result.addAll(commonReportParams);
		result.addAll(subscriberReportParams);

		result.forEach((s) -> s.getParamSpecialList().size());

		return result;
	}

    /**
     *
     * @param reportParamsetId
     * @return
     */
	@Transactional( readOnly = true)
	public ReportParamset findReportParamset(long reportParamsetId) {

		ReportParamset result = reportParamsetRepository.findById(reportParamsetId)
            .orElseThrow(() -> new EntityNotFoundException(ReportParamset.class, reportParamsetId));

		// result.getReportTemplate().getId();
		// result.getSubscriber().getId();

		result.getParamSpecialList().size();

		return result;

	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional( readOnly = true)
	public boolean checkCanUpdate(long id) {
		List<Long> ids = reportParamsetRepository.selectCommonParamsetIds();
		return ids.indexOf(id) == -1;
	}

	/**
	 *
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional
	public ReportParamset moveToArchive(long reportParamsetId) {

		if (!checkCanUpdate(reportParamsetId)) {
			return null;
		}

		ReportParamset rp = reportParamsetRepository.findById(reportParamsetId)
            .orElseThrow(() -> new EntityNotFoundException(ReportParamset.class, reportParamsetId));

		rp.set_active(false);
		rp.setActiveEndDate(new Date());
		ReportParamset result = reportParamsetRepository.save(rp);
		result.getParamSpecialList().size();

		return result;
	}

    /**
     *
     * @param srcId
     * @param reportParamset
     * @param contObjectIds
     * @param subscriber
     * @return
     */
	@Transactional
	public ReportParamset createByTemplate(long srcId, ReportParamset reportParamset, Long[] contObjectIds,
			Subscriber subscriber) {

		checkNotNull(reportParamset);
		checkArgument(reportParamset.isNew());

		checkNotNull(subscriber);
		checkArgument(!subscriber.isNew());

		ReportParamset src = reportParamsetRepository.findById(srcId)
            .orElseThrow(() -> new EntityNotFoundException(ReportParamset.class, srcId));


		ReportParamset rp = reportParamset;
		rp.setReportTemplate(src.getReportTemplate());
		rp.setSubscriber(subscriber);
		rp.setSrcReportParamsetId(srcId);
		rp.set_default(false);
		rp.set_active(true);
		rp.setActiveEndDate(null);

		rp.getParamSpecialList().forEach((i) -> {
			i.setId(null);
			i.setReportParamset(rp);
		});

		ReportParamset result = reportParamsetRepository.save(rp);

		if (contObjectIds != null) {
			updateUnitToParamset(result.getId(), contObjectIds);
		}

		result.getParamSpecialList().size();

		return result;
	}

	/**
	 *
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ContObjectDTO> selectParamsetContObjects(long reportParamsetId) {
        List<ContObject> contObjectList = reportParamsetUnitRepository.selectContObjects(reportParamsetId);
		return contObjectMapper.toDto(contObjectList);
	}

	/**
	 *
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<Long> selectReportParamsetObjectIds(long reportParamsetId) {
		return reportParamsetUnitRepository.selectObjectIds(reportParamsetId);
	}

	/**
	 *
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<Long> selectReportParamsetFilteredObjectIds(long reportParamsetId) {
		return reportParamsetUnitFilterRepository.selectObjectIds(reportParamsetId);
	}

	/**
	 *
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ContObjectDTO> selectParamsetAvailableContObjectUnits(long reportParamsetId, long subscriberId) {
		List<ContObject> preResult = reportParamsetUnitRepository.selectAvailableContObjects(reportParamsetId,
				subscriberId);
		return preResult.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(contObjectMapper::toDto).collect(Collectors.toList());
	}

    /**
     *
     * @param reportParamset
     * @param objectId
     * @return
     */
	@Transactional
	public ReportParamsetUnit addUnitToParamset(ReportParamset reportParamset, long objectId) {
		checkNotNull(reportParamset);
		checkArgument(!reportParamset.isNew());

		if (checkReportParamsetUnitObject(reportParamset.getId(), objectId)) {
			throw new PersistenceException(String.format(
					"ReportParamsetUnit error. A pair of ReportParamset (id=%d) and Object (id=%d) is alredy exists",
					reportParamset.getId(), objectId));
		}

		ReportParamsetUnit u = new ReportParamsetUnit();
		u.setObjectId(objectId);
		u.setReportParamset(reportParamset);
		ReportParamsetUnit result = reportParamsetUnitRepository.save(u);

		return result;
	}

	/**
	 *
	 * @param reportParamset
	 * @param objectIds
	 */
	@Transactional
	public void addUnitToParamset(ReportParamset reportParamset, long[] objectIds) {
		checkNotNull(reportParamset);
		checkArgument(!reportParamset.isNew());

		checkNotNull(objectIds);

		for (long id : objectIds) {
			addUnitToParamset(reportParamset, id);
		}
	}

    /**
     *
     * @param reportParamsetId
     * @param objectId
     * @return
     */
	@Transactional
	public ReportParamsetUnit addUnitToParamset(long reportParamsetId, Long objectId) {
		ReportParamset rp = findReportParamset(reportParamsetId);
		checkNotNull(rp);
		return addUnitToParamset(rp, objectId);
	}

    /**
     *
     * @param reportParamsetId
     * @param contObjectId
     */
	@Transactional
	public void deleteUnitFromParamset(final long reportParamsetId, final long contObjectId) {

		List<Long> ids = reportParamsetUnitRepository.selectUnitIds(reportParamsetId, contObjectId);

		if (ids.size() > 1) {
			log.trace("Can't delete ReportParamsetUnit. Too Many Rows. (reportParamsetId={}, contObjectId={})",
					reportParamsetId, contObjectId);
			throw new PersistenceException(String.format(
					"Can't delete ReportParamsetUnit. Too Many Rows. (reportParamsetId=%d, contObjectId=%d)",
					reportParamsetId, contObjectId));
		}
		if (ids.size() == 0) {
			log.trace("Can't delete ReportParamsetUnit. No Rows Found. (reportParamsetId={}, contObjectId={})",
					reportParamsetId, contObjectId);
			throw new PersistenceException(String.format(
					"Can't delete ReportParamsetUnit. No Rows Found. (reportParamsetId=%d, contObjectId=%d)",
					reportParamsetId, contObjectId));
		}

		reportParamsetUnitRepository.deleteById(ids.get(0));
	}

	/**
	 *
	 * @param reportParamsetId
	 * @param contObjectIds
	 */
	@Transactional
	public void deleteUnitFromParamset(long reportParamsetId, long[] contObjectIds) {
		checkNotNull(contObjectIds);
		for (long id : contObjectIds) {
			deleteUnitFromParamset(reportParamsetId, id);
		}
	}

	/**
	 *
	 * @param reportParamsetId
	 * @param unitId
	 * @return
	 */
	@Transactional( readOnly = true)
	public boolean checkReportParamsetUnitObject(long reportParamsetId, long unitId) {
		return reportParamsetUnitRepository.selectUnitIds(reportParamsetId, unitId).size() > 0;
	}

	/**
	 *
	 * @param reportTemplateId
	 * @param reportParamsetName
	 * @param reportPeriod
	 * @param reportOutputFileType
	 * @param subscriberId
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public ReportParamset createReportParamsetEx(long reportTemplateId, String reportParamsetName,
			ReportPeriodKey reportPeriod, ReportOutputFileType reportOutputFileType, long subscriberId,
			Boolean isAutoGenerated) {

		ReportTemplate reportTemplate = reportTemplateRepository.findById(reportTemplateId)
            .orElseThrow(() -> new EntityNotFoundException(ReportTemplate.class, reportTemplateId));

		Subscriber subscriber = subscriberService.selectSubscriber(subscriberId);
		checkNotNull(subscriber, String.format("Subscriber (id=%d) not found", subscriberId));

		return createReportParamsetEx(reportTemplate, reportParamsetName, reportPeriod, reportOutputFileType,
				subscriber, isAutoGenerated);

	}

	/**
	 *
	 * @param reportTemplate
	 * @param reportParamsetName
	 * @param reportPeriod
	 * @param reportOutputFileType
	 * @param subscriber
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public ReportParamset createReportParamsetEx(ReportTemplate reportTemplate, String reportParamsetName,
			ReportPeriodKey reportPeriod, ReportOutputFileType reportOutputFileType, Subscriber subscriber,
			Boolean isAutoGenerated) {

		checkNotNull(reportTemplate);
		checkNotNull(subscriber);

		ReportParamset reportParamset = new ReportParamset();
		reportParamset.setSubscriber(subscriber);
		reportParamset.setReportTemplate(reportTemplate);
		reportParamset.setName(reportParamsetName);
		reportParamset.setOutputFileType(reportOutputFileType);
		reportParamset.setReportPeriodKey(reportPeriod);
		reportParamset.setActiveStartDate(new Date());
		reportParamset.set_active(true);
		reportParamset.setIsAutoGenerated(isAutoGenerated);

		ReportParamset result = createReportParamset(reportParamset, null);

		result.getParamSpecialList().size();

		return result;
	}

	/**
	 *
	 * @param reportTemplateId
	 * @param activeDate
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ReportParamset> selectReportParamset(long reportTemplateId, DateTime activeDate) {
		return reportParamsetRepository.selectReportParamset(reportTemplateId, activeDate.toDate());
	}

    /**
     *
     * @param reportTemplateId
     * @param isActive
     * @return
     */
	@Transactional( readOnly = true)
	public List<ReportParamset> selectReportParamset(long reportTemplateId, boolean isActive) {
		return reportParamsetRepository.selectReportParamset(reportTemplateId, isActive);
	}

    /**
     *
     * @param reportParamsetId
     * @param objectIds
     */
	@Transactional
	public void updateUnitToParamset(final long reportParamsetId, final Long[] objectIds) {

		checkNotNull(objectIds);

		List<Long> newObjectIdList = Arrays.asList(objectIds);

		List<Long> currentIds = reportParamsetUnitRepository.selectObjectIds(reportParamsetId);
		for (Long currentId : currentIds) {
			if (!newObjectIdList.contains(currentId)) {
				log.trace("removing objectId:{}", currentId);
				deleteUnitFromParamset(reportParamsetId, currentId);
			}
		}

		for (Long newId : newObjectIdList) {
			if (!currentIds.contains(newId)) {
				addUnitToParamset(reportParamsetId, newId);
			}
		}

	}

	/**
	 *
	 */
	@Transactional
	public void setupRequiredPassed() {
		setupRequiredPassed(null);
	}

	/**
	 *
	 * @param reportParamsetId
	 */
	@Transactional
	public void setupRequiredPassed(Long reportParamsetId) {

		Iterable<ReportParamset> allParamsets = reportParamsetId == null ? reportParamsetRepository.findAll()
				: Arrays.asList(reportParamsetRepository.findById(reportParamsetId)
            .orElseThrow(() -> new EntityNotFoundException(ReportParamset.class,reportParamsetId ))
        );

		int totalCounter = 0;
		int passedCounter = 0;

		log.info("Setting up allRequiredParamsPassed property");
		for (ReportParamset rp : allParamsets) {

			if (rp == null) {
				continue;
			}

			ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(rp);

			boolean commonPassed = reportMakerParam.isAllCommonRequiredParamsExists();

			boolean specialPassed = reportMakerParam.isAllSpecialRequiredParamsExists();

			log.info("commonPassed:{}. specialPassed:{}.", commonPassed, specialPassed);

			boolean requiredPassed = commonPassed && specialPassed;

			if (requiredPassed) {
				passedCounter++;
			}

			log.info("ReportParamset id:{} reportType:{} ... requiredPass: {}", rp.getId(),
					rp.getReportTemplate().getReportTypeKeyname(), requiredPassed);

			rp.setAllRequiredParamsPassed(requiredPassed);
			reportParamsetRepository.save(rp);

			totalCounter++;
		}

		log.info("Total Paramset processed {}. Passed {}", totalCounter, passedCounter);

	}

    /**
     *
     * @param portalUserIds
     * @return
     */
	@Transactional( readOnly = true)
	public List<ReportParamset> selectReportParamsetContextLaunch(final PortalUserIds portalUserIds) {

		List<ReportParamset> result = null;
		if (portalUserIds.getSubscrTypeKey().isChild() && portalUserIds.haveParentSubacriber()) {
			result = reportParamsetRepository
					.selectRmaReportParamsetContextLaunchChild(portalUserIds.getParentSubscriberId());
		} else {
			result = reportParamsetRepository.selectReportParamsetContextLaunch(portalUserIds.getSubscriberId());
		}

		result.forEach((s) -> s.getParamSpecialList().size());
		return result;
	}

	/**
	 *
	 * @param reportParamsets
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ReportParamsetVO> wrapReportParamsetVO(final List<ReportParamset> reportParamsets) {
		List<ReportParamsetVO> result = reportParamsets.stream().map(i -> new ReportParamsetVO(i))
				.collect(Collectors.toList());

		List<ReportType> reportTypes = reportTypeService.findAllReportTypes();

		final Map<String, Integer> reportTypesMap = reportTypes.stream()
				.collect(Collectors.toMap(ReportType::getKeyname, item -> item.getReportTypeOrder()));

		result.forEach(i -> {
			i.setReportTypeOrder(reportTypesMap.get(i.getReportParamset().getReportTemplate().getReportTypeKeyname()));
		});

		return result;
	}

    /**
     *
     * @param subscriber
     * @return
     */
	@Transactional
	public List<ReportParamset> createDefaultReportParamsets(Subscriber subscriber) {

        Objects.requireNonNull(subscriber);
        Objects.requireNonNull(subscriber.getId());

		List<ReportParamset> resultList = new ArrayList<>();

		List<ReportType> reportTypes = reportTypeService.findAllReportTypes();

		for (ReportType rt : reportTypes) {
            log.debug("ReportType: {} check", rt.getKeyname());
			ReportTypeKey reportTypeKey = ReportTypeKey.valueOf(rt.getKeyname());
			if (reportTypeKey == null) {
				continue;
			}
            log.debug("ReportType: {} found. Looking for paramset", reportTypeKey);

			List<ReportParamset> existingReportParamset = reportParamsetRepository
					.selectSubscriberReportParamset(reportTypeKey.getKeyname(), true, subscriber.getId());

			if (!existingReportParamset.isEmpty()) {
                log.debug("ReportType: {} no Report Paramset", reportTypeKey);
				continue;
			}

            log.debug("ReportType: {} Looking for common templates", reportTypeKey);
			List<ReportTemplate> commonTemplates = reportTemplateRepository.selectCommonTemplates(reportTypeKey.getKeyname(),
                true);
			if (commonTemplates.isEmpty()) {
                log.debug("ReportType: {} Common templates not found", reportTypeKey);
				continue;
			}

			String suffix = systemParamService.getParamValueAsString(REPORT_AUTO_SUFFIX);

			String reportParamsetDefaultName = commonTemplates.get(0).getReportParamsetDefaultName() != null
					? commonTemplates.get(0).getReportParamsetDefaultName() : commonTemplates.get(0).getName();

			String reportParamsetName = reportParamsetDefaultName + " " + suffix;
            log.debug("ReportType: {} reportParamsetName: {}. Creating paramset", reportTypeKey, reportParamsetName);

			ReportParamset reportParamset = createReportParamsetEx(commonTemplates.get(0), reportParamsetName,
					ReportPeriodKey.CURRENT_MONTH, ReportOutputFileType.PDF, subscriber, true);

			resultList.add(reportParamset);
            log.debug("ReportType: {} Paramset init finished", reportTypeKey);

		}

		return resultList;
	}

	/**
	 *
	 * @param paramDirectoryKeyname
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ReportMetaParamDirectoryItem> selectReportMetaParamItems(String paramDirectoryKeyname) {
		return reportMetaParamDirectoryItemRepository.selectDirectoryItems(paramDirectoryKeyname);
	}

}
