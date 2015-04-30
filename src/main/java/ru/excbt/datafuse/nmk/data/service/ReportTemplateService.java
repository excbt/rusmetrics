package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.repository.ReportTemplateBodyRepository;
import ru.excbt.datafuse.nmk.data.repository.ReportTemplateRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class ReportTemplateService implements SecuredRoles {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportTemplateService.class);

	@Autowired
	private ReportTemplateRepository reportTemplateRepository;

	@Autowired
	private ReportTemplateBodyRepository reportTemplateBodyRepository;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private ReportMasterTemplateBodyService reportMasterTemplateBodyService;

	@Autowired
	private ReportTypeService reportTypeService;

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
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
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
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
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
	 * @return
	 */
	@Secured({ ROLE_ADMIN })
	public ReportTemplate updateOneCommon(ReportTemplate reportTemplate) {
		checkNotNull(reportTemplate);
		checkArgument(!reportTemplate.isNew());

		ReportTemplate result = null;
		if (checkIsCommon(reportTemplate.getId())) {
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
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(ReportTemplate reportTemplate) {
		checkNotNull(reportTemplate);
		checkArgument(!reportTemplate.isNew());

		deleteOne(reportTemplate.getId());
	}

	/**
	 * 
	 * @param reportTemplate
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(long reportTemplateId) {

		if (checkCanUpdate(reportTemplateId)) {
			if (reportTemplateBodyRepository.exists(reportTemplateId)) {
				reportTemplateBodyRepository.delete(reportTemplateId);
			}
			reportTemplateRepository.delete(reportTemplateId);
		} else {
			throw new PersistenceException(String.format(
					"Can't delete report template (id=%d)", reportTemplateId));
		}

	}

	/**
	 * 
	 * @param reportTemplate
	 */
	@Secured({ ROLE_ADMIN })
	public void deleteOneCommon(ReportTemplate reportTemplate) {
		checkNotNull(reportTemplate);
		checkArgument(!reportTemplate.isNew());

		deleteOneCommon(reportTemplate.getId());
	}

	/**
	 * 
	 * @param reportTemplate
	 */
	@Secured({ ROLE_ADMIN })
	public void deleteOneCommon(long reportTemplateId) {

		if (checkIsCommon(reportTemplateId)) {
			if (reportTemplateBodyRepository.exists(reportTemplateId)) {
				reportTemplateBodyRepository.delete(reportTemplateId);
			}
			reportTemplateRepository.delete(reportTemplateId);
		} else {
			throw new PersistenceException(String.format(
					"Can't delete report template (id=%d)", reportTemplateId));
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
	 * @param id
	 * @return
	 */
	public boolean checkIsCommon(long id) {
		return !checkCanUpdate(id);
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
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void saveReportTemplateBody(long reportTemplateId, byte[] body,
			String filename) {
		saveReportTemplateBodyInternal(reportTemplateId, body, filename, false);
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @param reportTemplateBody
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void saveReportTemplateBodyCompiled(long reportTemplateId,
			byte[] body, String filename) {

		saveReportTemplateBodyInternal(reportTemplateId, body, filename, true);
	}

	/**
	 * 
	 * @param srcReportTemplateId
	 * @return
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
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
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
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
	 * @param reportTemplateId
	 * @return
	 */
	@Transactional(readOnly = true)
	public ReportTemplateBody getReportTemplateBody(long reportTemplateId) {
		return reportTemplateBodyRepository.findOne(reportTemplateId);
	}

	/**
	 * 
	 * @param reportTypeKey
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ReportTemplate> selectActiveReportTemplates(
			ReportTypeKey reportTypeKey) {
		return reportTemplateRepository.selectActiveTemplates(reportTypeKey,
				true);
	}

	/**
	 * 
	 * @param reportTypeKey
	 * @param isActive
	 * @param isCompiled
	 */
	public int updateCommonReportTemplateBody(ReportTypeKey reportTypeKey,
			boolean isActive, boolean isCompiled) {
		List<ReportTemplate> updateCadidates = reportTemplateRepository
				.selectCommonTemplates(reportTypeKey, isActive);

		if (updateCadidates.size() == 0) {
			return 0;
		}

		for (ReportTemplate rt : updateCadidates) {
			logger.info("Cadidate to update: {}", rt.getId());
		}

		ReportMasterTemplateBody reportMasterTemplateBody = reportMasterTemplateBodyService
				.selectReportMasterTemplate(reportTypeKey);

		byte[] srcBody;
		String srcBodyFilename;
		if (isCompiled) {
			srcBody = reportMasterTemplateBody.getBodyCompiled();
			srcBodyFilename = reportMasterTemplateBody
					.getBodyCompiledFilename();
		} else {
			srcBody = reportMasterTemplateBody.getBody();
			srcBodyFilename = reportMasterTemplateBody.getBodyFilename();
		}
		logger.info("MasterTemplateBody size length: {}", srcBody.length);
		logger.info("MasterTemplateBody file name  : {}", srcBodyFilename);

		checkNotNull(srcBody);

		int resultIdx = 0;
		for (ReportTemplate rt : updateCadidates) {
			logger.info("Updating ReportTemplate:{}", rt.getId());

			saveReportTemplateBodyInternal(rt.getId(), srcBody,
					srcBodyFilename, isCompiled);
			resultIdx++;
		}

		return resultIdx;
	}

	/**
	 * 
	 * @param reportTypeKey
	 * @return
	 */
	@Secured({ ROLE_ADMIN })
	public ReportTemplate createCommonReportTemplate(ReportTypeKey reportTypeKey) {

		checkNotNull(reportTypeKey);

		ReportMasterTemplateBody masterTemplateBody = reportMasterTemplateBodyService
				.selectReportMasterTemplate(reportTypeKey);

		if (masterTemplateBody == null) {
			throw new PersistenceException(String.format(
					"ReportMasterTemplateBody for report %s not found",
					reportTypeKey.name()));
		}

		ReportTemplate reportTemplate = new ReportTemplate();

		reportTemplate.setReportTypeKey(reportTypeKey);
		ReportType reportType = reportTypeService.findByKeyname(reportTypeKey
				.name());
		if (reportType.getName() != null) {
			reportTemplate.setName(reportType.getName() + " (ОБЩИЙ)");
		} else {
			reportTemplate.setName(reportTypeKey.name() + " (ОБЩИЙ)");
		}

		reportTemplate.set_active(true);
		reportTemplate.set_default(true);
		reportTemplate.setActiveEndDate(new Date());
		ReportTemplate result = reportTemplateRepository.save(reportTemplate);

		ReportTemplateBody reportTemplateBody = new ReportTemplateBody();
		reportTemplateBody.setReportTemplateId(result.getId());
		reportTemplateBody.setBody(masterTemplateBody.getBody());
		reportTemplateBody
				.setBodyCompiled(masterTemplateBody.getBodyCompiled());
		reportTemplateBody
				.setBodyFilename(masterTemplateBody.getBodyFilename());
		reportTemplateBody.setBodyCompiledFilename(masterTemplateBody
				.getBodyCompiledFilename());

		reportTemplateBodyRepository.save(reportTemplateBody);

		return result;
	}

	/**
	 * 
	 * @param reportTypeKey
	 * @param isActive
	 * @param isCompiled
	 */
	public void updateTemplateBodyFromMaster(ReportTypeKey reportTypeKey,
			long reportTemplateId, boolean isCompiled) {

		ReportTemplate reportTemplate = reportTemplateRepository
				.findOne(reportTemplateId);
		checkNotNull(reportTemplate);

		ReportMasterTemplateBody reportMasterTemplateBody = reportMasterTemplateBodyService
				.selectReportMasterTemplate(reportTypeKey);

		byte[] srcBody;
		String srcBodyFilename;
		if (isCompiled) {
			srcBody = reportMasterTemplateBody.getBodyCompiled();
			srcBodyFilename = reportMasterTemplateBody
					.getBodyCompiledFilename();
		} else {
			srcBody = reportMasterTemplateBody.getBody();
			srcBodyFilename = reportMasterTemplateBody.getBodyFilename();
		}
		logger.info("MasterTemplateBody size length: {}", srcBody.length);
		logger.info("MasterTemplateBody file name  : {}", srcBodyFilename);

		checkNotNull(srcBody);

		logger.info("Updating ReportTemplate:{}", reportTemplate);

		saveReportTemplateBodyInternal(reportTemplate.getId(), srcBody,
				srcBodyFilename, isCompiled);

	}

}
