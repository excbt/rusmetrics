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
import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.model.ReportTemplateBody;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.ReportTemplateBodyRepository;
import ru.excbt.datafuse.nmk.data.repository.ReportTemplateRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class ReportTemplateService implements SecuredRoles {

	@Autowired
	private ReportTemplateRepository reportTemplateRepository;

	@Autowired
	private ReportTemplateBodyRepository reportTemplateBodyRepository;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private ReportMasterTemplateBodyService reportMasterTemplateBodyService;

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 */
	@Transactional(readOnly = true)
	public ReportTemplate findOne(long reportTemplateId) {
		ReportTemplate result = reportTemplateRepository
				.findOne(reportTemplateId);
		return result;
	}

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public ReportTemplate createOne(ReportTemplate reportTemplate) {
		checkNotNull(reportTemplate);
		checkArgument(reportTemplate.isNew());

		ReportTemplate result = reportTemplateRepository.save(reportTemplate);

		return result;
	}

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public ReportTemplate updateOne(ReportTemplate reportTemplate) {
		checkNotNull(reportTemplate);
		checkArgument(!reportTemplate.isNew());

		ReportTemplate result = null;
		if (checkCanUpdate(reportTemplate.getId())) {
			result = reportTemplateRepository.save(reportTemplate);
		} else {
			throw new PersistenceException(String.format(
					"Can't update common template (id=%d)",
					reportTemplate.getId()));
		}

		return result;
	}

	/**
	 * 
	 * @param reportTemplate
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void deleteOne(ReportTemplate reportTemplate) {
		checkNotNull(reportTemplate);
		checkArgument(!reportTemplate.isNew());

		if (checkCanUpdate(reportTemplate.getId())) {
			if (reportTemplateBodyRepository.exists(reportTemplate.getId())) {
				reportTemplateBodyRepository.delete(reportTemplate.getId());
			}
			reportTemplateRepository.delete(reportTemplate);
		} else {
			throw new PersistenceException(String.format(
					"Can't delete common template (id=%d)",
					reportTemplate.getId()));
		}

	}

	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<ReportTemplate> selectDefaultReportTemplates(
			ReportTypeKey reportType, boolean isActive) {
		return reportTemplateRepository.selectCommonTemplates(reportType,
				isActive);
	}

	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<ReportTemplate> selectSubscriberReportTemplates(
			ReportTypeKey reportType, boolean isActive, long subscriberId) {

		List<ReportTemplate> result = reportTemplateRepository
				.selectSubscriberTemplates(reportType, isActive, subscriberId);

		return result;
	}

	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<ReportTemplate> getAllReportTemplates(long subscriberId,
			ReportTypeKey reportType, boolean isActive) {

		List<ReportTemplate> result = new ArrayList<>();
		List<ReportTemplate> commonTemplates = selectDefaultReportTemplates(
				reportType, isActive);

		List<ReportTemplate> subscriberTemplates = selectSubscriberReportTemplates(
				reportType, isActive, subscriberId);

		result.addAll(commonTemplates);
		result.addAll(subscriberTemplates);

		return result;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public boolean checkCanUpdate(long id) {
		List<Long> ids = reportTemplateRepository.selectCommonTemplateIds();
		return ids.indexOf(id) == -1;
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @param body
	 * @param filename
	 * @param isCompiled
	 */
	private void saveReportTemplateBodyInternal(long reportTemplateId,
			byte[] body, String filename, boolean isCompiled) {
		ReportTemplateBody rtb = reportTemplateBodyRepository
				.findOne(reportTemplateId);
		if (rtb == null) {
			rtb = new ReportTemplateBody();
			rtb.setReportTemplateId(reportTemplateId);
		}
		if (isCompiled) {
			rtb.setBodyCompiled(body);
			rtb.setBodyCompiledFilename(filename);
		} else {
			rtb.setBody(body);
			rtb.setBodyFilename(filename);
		}
		reportTemplateBodyRepository.save(rtb);
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @param reportTemplateBody
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void saveReportTemplateBody(long reportTemplateId, byte[] body,
			String filename) {
		saveReportTemplateBodyInternal(reportTemplateId, body, filename, false);
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @param reportTemplateBody
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void saveReportTemplateBodyCompiled(long reportTemplateId,
			byte[] body, String filename) {

		saveReportTemplateBodyInternal(reportTemplateId, body, filename, true);
	}

	/**
	 * 
	 * @param srcReportTemplateId
	 * @return
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public ReportTemplate createByTemplate(long srcId,
			ReportTemplate reportTemplate, Subscriber subscriber) {

		checkNotNull(reportTemplate);
		checkArgument(reportTemplate.isNew());

		checkNotNull(subscriber);
		checkArgument(!subscriber.isNew());

		ReportTemplate srcReportTemplate = reportTemplateRepository
				.findOne(srcId);
		checkNotNull(srcReportTemplate, "Report Template not found. id="
				+ srcId);

		reportTemplate.setReportTypeKey(srcReportTemplate.getReportTypeKey());
		reportTemplate.setSubscriber(subscriber);
		reportTemplate.setSrcReportTemplateId(srcId);
		reportTemplate.setIntegratorIncluded(srcReportTemplate
				.getIntegratorIncluded());
		reportTemplate.set_default(false);
		reportTemplate.set_active(true);
		reportTemplate.setActiveEndDate(null);
		reportTemplate.setActiveStartDate(new Date());

		ReportTemplate result = reportTemplateRepository.save(reportTemplate);

		ReportTemplateBody srcBody = getReportTemplateBody(srcReportTemplate
				.getId());
		if (srcBody != null) {
			ReportTemplateBody newBody = new ReportTemplateBody();
			newBody.setReportTemplateId(result.getId());
			newBody.setBody(srcBody.getBody());
			newBody.setBodyFilename(srcBody.getBodyFilename());
			newBody.setBodyCompiled(srcBody.getBodyCompiled());
			newBody.setBodyCompiledFilename(srcBody.getBodyCompiledFilename());
			reportTemplateBodyRepository.save(newBody);
		}

		return result;
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public ReportTemplate moveToArchive(long reportTemplateId) {

		if (!checkCanUpdate(reportTemplateId)) {
			throw new PersistenceException(String.format(
					"ReportTemplate with (id=%d) is not updatable",
					reportTemplateId));
		}

		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportParamset(reportTemplateId, true);

		if (reportParamsetList.size() > 0) {
			return null;
		}

		ReportTemplate rt = reportTemplateRepository.findOne(reportTemplateId);
		if (rt == null) {
			throw new PersistenceException(String.format(
					"ReportTemplate (id=%d) not found", reportTemplateId));
		}
		if (!rt.is_active()) {
			throw new PersistenceException(String.format(
					"ReportTemplate (id=%d) is alredy archived",
					reportTemplateId));
		}
		rt.set_active(false);
		rt.setActiveEndDate(new Date());
		ReportTemplate result = reportTemplateRepository.save(rt);
		return result;

	}

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public ReportTemplate createCommerceWizard(ReportTemplate reportTemplate,
			Subscriber subscriber) {

		checkNotNull(reportTemplate);
		checkArgument(reportTemplate.isNew());
		checkNotNull(subscriber);
		checkArgument(!subscriber.isNew());

		if (reportTemplate.getIntegratorIncluded() == null) {
			reportTemplate.setIntegratorIncluded(false);
		}

		reportTemplate.setSubscriber(subscriber);
		reportTemplate.setReportTypeKey(ReportTypeKey.COMMERCE_REPORT);

		ReportMasterTemplateBody masterBody = reportMasterTemplateBodyService
				.selectReportMasterTemplate(ReportTypeKey.COMMERCE_REPORT);

		if (masterBody == null) {
			throw new PersistenceException(String.format(
					"ReportMasterTemplate for %s not found",
					ReportTypeKey.COMMERCE_REPORT.name()));
		}

		ReportTemplate resultEntity = reportTemplateRepository
				.save(reportTemplate);

		saveReportTemplateBodyCompiled(resultEntity.getId(),
				masterBody.getBodyCompiled(), masterBody.getBodyFilename());

		return resultEntity;
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 */
	@Transactional(readOnly = true)
	public ReportTemplateBody getReportTemplateBody(long reportTemplateId) {
		return reportTemplateBodyRepository.findOne(reportTemplateId);
	}

}
