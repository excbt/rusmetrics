package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportOutputFileType;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportPeriodKey;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetUnit;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.ReportParamsetRepository;
import ru.excbt.datafuse.nmk.data.repository.ReportParamsetUnitRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class ReportParamsetService implements SecuredRoles {

	@Autowired
	private ReportParamsetRepository reportParamsetRepository;

	@Autowired
	private ReportParamsetUnitRepository reportParamsetUnitRepository;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public ReportParamset createOne(ReportParamset entity) {
		checkNotNull(entity);
		checkArgument(entity.isNew());

		ReportParamset result = reportParamsetRepository.save(entity);

		return result;
	}

	/**
	 * 
	 * @param entity
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void deleteOne(ReportParamset entity) {
		checkNotNull(entity);
		if (checkCanUpdate(entity.getId())) {
			reportParamsetRepository.delete(entity);
		} else {
			throw new PersistenceException(String.format(
					"Can't delete ReportParamset(id=%d)", entity.getId()));
		}

	}

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public ReportParamset updateOne(ReportParamset reportParamset) {
		checkNotNull(reportParamset);
		checkArgument(!reportParamset.isNew());

		ReportParamset result = null;
		if (checkCanUpdate(reportParamset.getId())) {
			result = reportParamsetRepository.save(reportParamset);
		} else {
			throw new PersistenceException(String.format(
					"Can't update common Report Paramset (id=%d)",
					reportParamset.getId()));
		}

		return result;
	}

	/**
	 * 
	 * @param id
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void deleteOne(long id) {
		if (checkCanUpdate(id)) {
			reportParamsetRepository.delete(id);
		} else {
			throw new PersistenceException(String.format(
					"Can't delete ReportParamset(id=%d)", id));
		}

	}

	/**
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ReportParamset> findReportParamsetList(long reportTemplateId) {
		return reportParamsetRepository
				.findByReportTemplateId(reportTemplateId);
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ReportParamset> selectReportTypeParamsetList(
			ReportTypeKey reportType, boolean isActive, long subscriberId) {
		List<ReportParamset> commonReportParams = reportParamsetRepository
				.selectCommonReportParamset(reportType, isActive);
		List<ReportParamset> subscriberReportParams = reportParamsetRepository
				.selectSubscriberReportParamset(reportType, isActive,
						subscriberId);

		List<ReportParamset> result = new ArrayList<>();
		result.addAll(commonReportParams);
		result.addAll(subscriberReportParams);
		return result;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public ReportParamset findOne(long reportParamsetId) {
		ReportParamset result = reportParamsetRepository
				.findOne(reportParamsetId);
		if (result.getReportTemplate() != null) {
			result.getReportTemplate().getId();
		}

		return result;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public boolean checkCanUpdate(long id) {
		List<Long> ids = reportParamsetRepository.selectCommonParamsetIds();
		return ids.indexOf(id) == -1;
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 */
	public ReportParamset moveToArchive(long reportParamsetId) {

		if (!checkCanUpdate(reportParamsetId)) {
			return null;
		}

		ReportParamset rp = reportParamsetRepository.findOne(reportParamsetId);
		if (rp == null) {
			throw new PersistenceException(String.format(
					"ReportParamset (id=%d) not found", reportParamsetId));
		}
		rp.set_active(false);
		rp.setActiveEndDate(new Date());
		ReportParamset result = reportParamsetRepository.save(rp);
		return result;
	}

	/**
	 * 
	 * @param srcReportTemplateId
	 * @return
	 */
	public ReportParamset createByTemplate(long srcId,
			ReportParamset reportParamset, Subscriber subscriber) {

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

		ReportParamset result = reportParamsetRepository.save(rp);

		copyReportParamsetUnit(srcId, result.getId());

		return result;
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	public List<ContObject> selectParamsetContObjectUnits(long reportParamsetId) {
		return reportParamsetUnitRepository
				.selectContObjectUnits(reportParamsetId);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	public List<ContObject> selectParamsetAvailableContObjectUnits(
			long reportParamsetId, long subscriberId) {
		return reportParamsetUnitRepository.selectAvailableContObjectUnits(
				reportParamsetId, subscriberId);
	}

	/**
	 * 
	 * @param contObject
	 * @return
	 */
	public ReportParamsetUnit addUnitToParamset(ReportParamset reportParamset,
			long objectId) {
		checkNotNull(reportParamset);
		checkArgument(!reportParamset.isNew());

		if (checkReportParamsetUnitObject(reportParamset.getId(), objectId)) {
			throw new PersistenceException(
					String.format(
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
	public void addUnitToParamset(ReportParamset reportParamset,
			long[] objectIds) {
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
	public ReportParamsetUnit addUnitToParamset(long reportParamsetId,
			Long objectId) {
		ReportParamset rp = findOne(reportParamsetId);
		checkNotNull(rp);
		return addUnitToParamset(rp, objectId);
	}

	/**
	 * 
	 * @param reportParamsetUnitId
	 */
	public void deleteUnitFromParamset(long reportParamsetId, long contObjectId) {

		List<Long> ids = reportParamsetUnitRepository.selectObjectIds(
				reportParamsetId, contObjectId);

		if (ids.size() != 1) {
			throw new PersistenceException(
					String.format(
							"Can't delete ReportParamsetUnit. (reportParamsetId=%d, contObjectId=%d)",
							reportParamsetId, contObjectId));
		}

		reportParamsetUnitRepository.delete(ids.get(0));
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param contObjectIds
	 */
	public void deleteUnitFromParamset(long reportParamsetId,
			long[] contObjectIds) {
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
	public boolean checkReportParamsetUnitObject(long reportParamsetId,
			long objectId) {
		return reportParamsetUnitRepository.selectObjectIds(reportParamsetId,
				objectId).size() > 0;
	}

	/**
	 * 
	 * @param srcReportParamsetId
	 * @param dstReportParamsetId
	 * @return
	 */
	public void copyReportParamsetUnit(long srcReportParamsetId,
			long dstReportParamsetId) {

		List<?> checkList = reportParamsetUnitRepository
				.selectObjectIds(dstReportParamsetId);
		if (checkList.size() > 0) {
			throw new PersistenceException(
					String.format(
							"ReportParamsetUnit of ReportParamset(id=%d) already exists",
							dstReportParamsetId));
		}

		ReportParamset dstReportParamset = reportParamsetRepository
				.findOne(dstReportParamsetId);
		checkNotNull(dstReportParamset);

		List<Long> idsToCopy = reportParamsetUnitRepository
				.selectObjectIds(srcReportParamsetId);
		for (Long id : idsToCopy) {
			addUnitToParamset(dstReportParamset, id);
		}
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 */
	public ReportParamset createReportParamsetMaster(long reportTemplateId,
			String reportParamsetName, ReportPeriodKey reportPeriod,
			ReportOutputFileType reportOutputFileType, long subscriberId) {

		ReportTemplate reportTemplate = reportTemplateService
				.findOne(reportTemplateId);
		checkNotNull(reportTemplate, String.format(
				"ReportTemplate (id=%d) not found", reportTemplateId));

		Subscriber subscriber = subscriberService.findOne(subscriberId);
		checkNotNull(subscriber,
				String.format("Subscriber (id=%d) not found", subscriberId));

		ReportParamset reportParamset = new ReportParamset();
		reportParamset.setSubscriber(subscriber);
		reportParamset.setReportTemplate(reportTemplate);
		reportParamset.setName(reportParamsetName);
		reportParamset.setOutputFileType(reportOutputFileType);
		reportParamset.setReportPeriodKey(reportPeriod);
		reportParamset.setActiveStartDate(new Date());
		reportParamset.set_active(true);

		ReportParamset result = createOne(reportParamset);

		return result;
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @param activeDate
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ReportParamset> selectReportParamset(long reportTemplateId,
			DateTime activeDate) {
		return reportParamsetRepository.selectReportParamset(reportTemplateId,
				activeDate.toDate());
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @param activeDate
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ReportParamset> selectReportParamset(long reportTemplateId,
			boolean isActive) {
		return reportParamsetRepository.selectReportParamset(reportTemplateId,
				isActive);
	}

}
