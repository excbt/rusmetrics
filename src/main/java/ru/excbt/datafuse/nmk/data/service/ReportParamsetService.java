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

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKeys;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.repository.ReportParamsetRepository;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class ReportParamsetService implements SecuredRoles {

	@Autowired
	private ReportParamsetRepository reportParamsetRepository;

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
			throw new PersistenceException(String.format("Can't delete ReportParamset(id=%d)", entity.getId()));
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
			throw new PersistenceException(String.format("Can't delete ReportParamset(id=%d)", id));
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
			ReportTypeKeys reportType, boolean isActive) {
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
	public ReportParamset createByReportParamset(long srcId,
			ReportParamset reportParamset) {

		checkNotNull(reportParamset);
		checkArgument(reportParamset.isNew());

		ReportParamset src = reportParamsetRepository
				.findOne(srcId);

		checkNotNull(src, "ReportParamset not found. id="
				+ srcId);

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
	
}
