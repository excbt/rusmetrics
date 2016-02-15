package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetUnit;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.data.repository.ReportParamsetRepository;
import ru.excbt.datafuse.nmk.data.repository.ReportParamsetUnitFilterRepository;
import ru.excbt.datafuse.nmk.data.repository.ReportParamsetUnitRepository;
import ru.excbt.datafuse.nmk.report.ReportOutputFileType;
import ru.excbt.datafuse.nmk.report.ReportPeriodKey;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;

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

	private static final Logger logger = LoggerFactory.getLogger(ReportParamsetService.class);

	@Autowired
	private ReportParamsetRepository reportParamsetRepository;

	@Autowired
	private ReportParamsetUnitRepository reportParamsetUnitRepository;

	@Autowired
	private ReportParamsetUnitFilterRepository reportParamsetUnitFilterRepository;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private ReportSheduleService reportSheduleService;

	@Autowired
	private ReportMakerParamService reportMakerParamService;

	/**
	 * 
	 * @param reportParamset
	 * @return
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	protected ReportParamset createOne(ReportParamset reportParamset) {
		checkNotNull(reportParamset);
		checkArgument(reportParamset.isNew());

		ReportParamset result = reportParamsetRepository.save(reportParamset);

		return result;
	}

	/**
	 * 
	 * @param reportParamset
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public ReportParamset createOne(ReportParamset reportParamset, Long[] contObjectIds) {

		checkNotNull(reportParamset);

		for (ReportParamsetParamSpecial param : reportParamset.getParamSpecialList()) {
			param.setReportParamset(reportParamset);
		}

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamset, contObjectIds);

		boolean requiredPassed = reportMakerParamService.isAllCommonRequiredParamsExists(reportMakerParam)
				&& reportMakerParamService.isAllSpecialRequiredParamsExists(reportMakerParam);

		reportParamset.setAllRequiredParamsPassed(requiredPassed);

		ReportParamset result = createOne(reportParamset);

		if (contObjectIds != null) {
			updateUnitToParamset(result.getId(), contObjectIds);
		}
		return result;
	}

	/**
	 * 
	 * @param entity
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(ReportParamset entity) {
		checkNotNull(entity);
		if (checkCanUpdate(entity.getId())) {
			reportParamsetRepository.delete(entity);
		} else {
			throw new PersistenceException(String.format("Can't delete ReportParamset(id=%d)", entity.getId()));
		}

	}

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	protected ReportParamset updateOne(ReportParamset reportParamset) {
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

		boolean requiredPassed = reportMakerParamService.isAllCommonRequiredParamsExists(reportMakerParam)
				&& reportMakerParamService.isAllSpecialRequiredParamsExists(reportMakerParam);

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
	 * @param reportTemplate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public ReportParamset updateOne(ReportParamset reportParamset, Long[] contObjectIds) {

		checkNotNull(reportParamset);

		ReportParamset result = updateOne(reportParamset);

		if (contObjectIds != null) {
			updateUnitToParamset(result.getId(), contObjectIds);
		}
		return result;
	}

	/**
	 * 
	 * @param id
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(long id) {
		if (checkCanUpdate(id)) {
			reportParamsetUnitRepository.softDeleteByReportParamset(id);
			reportSheduleService.deleteByReportParamset(id);
			reportParamsetRepository.delete(id);
		} else {
			throw new PersistenceException(String.format("Can't delete ReportParamset(id=%d)", id));
		}

	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ReportParamset> findReportParamsetList(long reportTemplateId) {
		return reportParamsetRepository.findByReportTemplateId(reportTemplateId);
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
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
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportParamset findOne(long reportParamsetId) {

		ReportParamset result = reportParamsetRepository.findOne(reportParamsetId);

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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean checkCanUpdate(long id) {
		List<Long> ids = reportParamsetRepository.selectCommonParamsetIds();
		return ids.indexOf(id) == -1;
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public ReportParamset moveToArchive(long reportParamsetId) {

		if (!checkCanUpdate(reportParamsetId)) {
			return null;
		}

		ReportParamset rp = reportParamsetRepository.findOne(reportParamsetId);
		if (rp == null) {
			throw new PersistenceException(String.format("ReportParamset (id=%d) not found", reportParamsetId));
		}
		rp.set_active(false);
		rp.setActiveEndDate(new Date());
		ReportParamset result = reportParamsetRepository.save(rp);
		result.getParamSpecialList().size();

		return result;
	}

	/**
	 * 
	 * @param srcReportTemplateId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public ReportParamset createByTemplate(long srcId, ReportParamset reportParamset, Long[] contObjectIds,
			Subscriber subscriber) {

		checkNotNull(reportParamset);
		checkArgument(reportParamset.isNew());

		checkNotNull(subscriber);
		checkArgument(!subscriber.isNew());

		ReportParamset src = reportParamsetRepository.findOne(srcId);

		checkNotNull(src, "ReportParamset not found. id=" + srcId);

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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectParamsetContObjects(long reportParamsetId) {
		return reportParamsetUnitRepository.selectContObjects(reportParamsetId);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectReportParamsetObjectIds(long reportParamsetId) {
		return reportParamsetUnitRepository.selectObjectIds(reportParamsetId);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectReportParamsetFilteredObjectIds(long reportParamsetId) {
		return reportParamsetUnitFilterRepository.selectObjectIds(reportParamsetId);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectParamsetAvailableContObjectUnits(long reportParamsetId, long subscriberId) {
		List<ContObject> preResult = reportParamsetUnitRepository.selectAvailableContObjects(reportParamsetId,
				subscriberId);
		return ObjectFilters.deletedFilter(preResult);
	}

	/**
	 * 
	 * @param contObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
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
	@Transactional(value = TxConst.TX_DEFAULT)
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
	 * @param contObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public ReportParamsetUnit addUnitToParamset(long reportParamsetId, Long objectId) {
		ReportParamset rp = findOne(reportParamsetId);
		checkNotNull(rp);
		return addUnitToParamset(rp, objectId);
	}

	/**
	 * 
	 * @param reportParamsetUnitId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteUnitFromParamset(final long reportParamsetId, final long contObjectId) {

		List<Long> ids = reportParamsetUnitRepository.selectUnitIds(reportParamsetId, contObjectId);

		if (ids.size() > 1) {
			logger.trace("Can't delete ReportParamsetUnit. Too Many Rows. (reportParamsetId={}, contObjectId={})",
					reportParamsetId, contObjectId);
			throw new PersistenceException(String.format(
					"Can't delete ReportParamsetUnit. Too Many Rows. (reportParamsetId=%d, contObjectId=%d)",
					reportParamsetId, contObjectId));
		}
		if (ids.size() == 0) {
			logger.trace("Can't delete ReportParamsetUnit. No Rows Found. (reportParamsetId={}, contObjectId={})",
					reportParamsetId, contObjectId);
			throw new PersistenceException(String.format(
					"Can't delete ReportParamsetUnit. No Rows Found. (reportParamsetId=%d, contObjectId=%d)",
					reportParamsetId, contObjectId));
		}

		reportParamsetUnitRepository.delete(ids.get(0));
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectIds
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteUnitFromParamset(long reportParamsetId, long[] contObjectIds) {
		checkNotNull(contObjectIds);
		for (long id : contObjectIds) {
			deleteUnitFromParamset(reportParamsetId, id);
		}
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param objectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean checkReportParamsetUnitObject(long reportParamsetId, long objectId) {
		return reportParamsetUnitRepository.selectUnitIds(reportParamsetId, objectId).size() > 0;
	}

	/**
	 * 
	 * @param srcReportParamsetId
	 * @param dstReportParamsetId
	 * @return
	 */
	@Deprecated
	@Transactional(value = TxConst.TX_DEFAULT)
	public void copyReportParamsetUnit222(long srcReportParamsetId, long dstReportParamsetId) {

		List<?> checkList = reportParamsetUnitRepository.selectUnitIds(dstReportParamsetId);
		if (checkList.size() > 0) {
			throw new PersistenceException(
					String.format("ReportParamsetUnit of ReportParamset(id=%d) already exists", dstReportParamsetId));
		}

		ReportParamset dstReportParamset = reportParamsetRepository.findOne(dstReportParamsetId);
		checkNotNull(dstReportParamset);

		List<Long> idsToCopy = reportParamsetUnitRepository.selectUnitIds(srcReportParamsetId);
		for (Long id : idsToCopy) {
			addUnitToParamset(dstReportParamset, id);
		}
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public ReportParamset createReportParamsetMaster(long reportTemplateId, String reportParamsetName,
			ReportPeriodKey reportPeriod, ReportOutputFileType reportOutputFileType, long subscriberId) {

		ReportTemplate reportTemplate = reportTemplateService.findOne(reportTemplateId);
		checkNotNull(reportTemplate, String.format("ReportTemplate (id=%d) not found", reportTemplateId));

		Subscriber subscriber = subscriberService.selectSubscriber(subscriberId);
		checkNotNull(subscriber, String.format("Subscriber (id=%d) not found", subscriberId));

		ReportParamset reportParamset = new ReportParamset();
		reportParamset.setSubscriber(subscriber);
		reportParamset.setReportTemplate(reportTemplate);
		reportParamset.setName(reportParamsetName);
		reportParamset.setOutputFileType(reportOutputFileType);
		reportParamset.setReportPeriodKey(reportPeriod);
		reportParamset.setActiveStartDate(new Date());
		reportParamset.set_active(true);

		ReportParamset result = createOne(reportParamset);

		result.getParamSpecialList().size();

		return result;
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @param activeDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ReportParamset> selectReportParamset(long reportTemplateId, DateTime activeDate) {
		return reportParamsetRepository.selectReportParamset(reportTemplateId, activeDate.toDate());
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @param activeDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ReportParamset> selectReportParamset(long reportTemplateId, boolean isActive) {
		return reportParamsetRepository.selectReportParamset(reportTemplateId, isActive);
	}

	/**
	 * 
	 * @param contObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void updateUnitToParamset(final long reportParamsetId, final Long[] objectIds) {

		checkNotNull(objectIds);

		List<Long> newObjectIdList = Arrays.asList(objectIds);

		List<Long> currentIds = reportParamsetUnitRepository.selectObjectIds(reportParamsetId);
		for (Long currentId : currentIds) {
			if (!newObjectIdList.contains(currentId)) {
				logger.trace("removing objectId:{}", currentId);
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
	@Transactional(value = TxConst.TX_DEFAULT)
	public void setupRequiredPassed() {
		setupRequiredPassed(null);
	}

	/**
	 * 
	 * @param reportParamsetId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void setupRequiredPassed(Long reportParamsetId) {

		Iterable<ReportParamset> allParamsets = reportParamsetId == null ? reportParamsetRepository.findAll()
				: Arrays.asList(reportParamsetRepository.findOne(reportParamsetId));

		int totalCounter = 0;
		int passedCounter = 0;

		logger.info("Setting up allRequiredParamsPassed property");
		for (ReportParamset rp : allParamsets) {

			if (rp == null) {
				continue;
			}

			ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(rp);

			boolean commonPassed = reportMakerParamService.isAllCommonRequiredParamsExists(reportMakerParam);

			boolean specialPassed = reportMakerParamService.isAllSpecialRequiredParamsExists(reportMakerParam);

			logger.info("commonPassed:{}. specialPassed:{}.", commonPassed, specialPassed);

			boolean requiredPassed = commonPassed && specialPassed;

			if (requiredPassed) {
				passedCounter++;
			}

			logger.info("ReportParamset id:{} reportType:{} ... requiredPass: {}", rp.getId(),
					rp.getReportTemplate().getReportTypeKeyname(), requiredPassed);

			rp.setAllRequiredParamsPassed(requiredPassed);
			reportParamsetRepository.save(rp);

			totalCounter++;
		}

		logger.info("Total Paramset processed {}. Passed {}", totalCounter, passedCounter);

	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ReportParamset> selectReportParamsetContextLaunch(Long subscriberId) {
		List<ReportParamset> result = reportParamsetRepository.selectReportParamsetContextLaunch(subscriberId);
		result.forEach((s) -> s.getParamSpecialList().size());
		return result;
	}

}
