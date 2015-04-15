package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetUnit;
import ru.excbt.datafuse.nmk.data.repository.ReportParamsetRepository;
import ru.excbt.datafuse.nmk.data.repository.ReportParamsetUnitRepository;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class ReportParamsetService implements SecuredRoles {

	@Autowired
	private ReportParamsetRepository reportParamsetRepository;

	@Autowired
	private ReportParamsetUnitRepository reportParamsetUnitRepository;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

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
			ReportTypeKey reportType, boolean isActive) {
		List<ReportParamset> commonReportParams = reportParamsetRepository
				.selectCommonReportParamset(reportType, isActive);
		List<ReportParamset> subscriberReportParams = reportParamsetRepository
				.selectSubscriberReportParamset(
						currentSubscriberService.getSubscriberId(), reportType,
						isActive);

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
	public ReportParamset findOne(long id) {
		ReportParamset result = reportParamsetRepository.findOne(id);
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
			ReportParamset reportParamset) {

		checkNotNull(reportParamset);
		checkArgument(reportParamset.isNew());

		ReportParamset src = reportParamsetRepository.findOne(srcId);

		checkNotNull(src, "ReportParamset not found. id=" + srcId);

		ReportParamset rp = reportParamset;
		rp.setReportTemplate(src.getReportTemplate());
		rp.setSubscriber(currentSubscriberService.getSubscriber());
		rp.setSrcReportParamsetId(srcId);
		rp.set_default(false);
		rp.set_active(true);
		rp.setActiveEndDate(null);

		ReportParamset result = reportParamsetRepository.save(rp);

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
			long reportParamsetId) {
		return reportParamsetUnitRepository.selectAvailableContObjectUnits(
				reportParamsetId, currentSubscriberService.getSubscriberId());
	}

	/**
	 * 
	 * @param contObject
	 * @return
	 */
	public ReportParamsetUnit addUnitToParamset(ReportParamset reportParamset,
			Long objectId) {
		checkNotNull(reportParamset);
		checkArgument(!reportParamset.isNew());

		checkNotNull(objectId);

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
	public void deleteUnitFromParamset(long reportParamsetId,
			long reportParamsetUnitId) {
		ReportParamsetUnit unit = reportParamsetUnitRepository
				.findOne(reportParamsetUnitId);
		if (unit.getReportParamset().getId() != reportParamsetId) {
			throw new PersistenceException(
					String.format(
							"Can't delete ReportParamsetUnit(id=%d) from ReportParamset (id=%d)",
							reportParamsetUnitId, reportParamsetId));
		}

		reportParamsetUnitRepository.delete(reportParamsetUnitId);
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
}
