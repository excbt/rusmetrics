package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.repository.ReportParamsetRepository;
import ru.excbt.datafuse.nmk.data.repository.ReportTemplateBodyRepository;
import ru.excbt.datafuse.nmk.data.repository.ReportTemplateRepository;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.security.AuthoritiesConstants;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Сервис для работы с шаблонами отчета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.04.2015
 *
 */
@Service
public class ReportTemplateService {

	private static final Logger logger = LoggerFactory.getLogger(ReportTemplateService.class);

	private final ReportTemplateRepository reportTemplateRepository;

	private final ReportTemplateBodyRepository reportTemplateBodyRepository;

	private final ReportParamsetRepository reportParamsetRepository;

	private final ReportMasterTemplateBodyService reportMasterTemplateBodyService;

	private final ReportTypeService reportTypeService;

    public ReportTemplateService(ReportTemplateRepository reportTemplateRepository, ReportTemplateBodyRepository reportTemplateBodyRepository, ReportParamsetRepository reportParamsetRepository, ReportMasterTemplateBodyService reportMasterTemplateBodyService, ReportTypeService reportTypeService) {
        this.reportTemplateRepository = reportTemplateRepository;
        this.reportTemplateBodyRepository = reportTemplateBodyRepository;
        this.reportParamsetRepository = reportParamsetRepository;
        this.reportMasterTemplateBodyService = reportMasterTemplateBodyService;
        this.reportTypeService = reportTypeService;
    }

    /**
	 *
	 * @param reportTemplateId
	 * @return
	 */
	@Transactional( readOnly = true)
	public ReportTemplate findOne(long reportTemplateId) {
		ReportTemplate result = reportTemplateRepository.findById(reportTemplateId)
            .orElseThrow(() -> new EntityNotFoundException(ReportTemplate.class, reportTemplateId));
		return result;
	}

	/**
	 *
	 * @param reportTemplate
	 * @return
	 */
	@Transactional
	@Secured({AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public ReportTemplate createOne(ReportTemplate reportTemplate) {
		checkNotNull(reportTemplate);
		checkArgument(reportTemplate.isNew());
		checkNotNull(reportTemplate.getSubscriber());

		ReportTemplate result = reportTemplateRepository.save(reportTemplate);

		return result;
	}

	/**
	 *
	 * @param reportTemplate
	 * @return
	 */
	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public ReportTemplate updateOne(ReportTemplate reportTemplate) {
		checkNotNull(reportTemplate);
		checkArgument(!reportTemplate.isNew());
		checkNotNull(reportTemplate.getSubscriber());

		ReportTemplate result = null;

		if (checkCanUpdate(reportTemplate.getId())) {
			result = reportTemplateRepository.save(reportTemplate);
		} else {
			throw new PersistenceException(
					String.format("Can't update common template (id=%d)", reportTemplate.getId()));
		}

		return result;
	}

	/**
	 *
	 * @param reportTemplate
	 * @return
	 */
	protected ReportTemplate updateOneAny(ReportTemplate reportTemplate) {
		checkNotNull(reportTemplate);
		checkArgument(!reportTemplate.isNew());

		ReportTemplate result = reportTemplateRepository.save(reportTemplate);

		return result;
	}

	/**
	 *
	 * @param reportTemplate
	 */
	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public void deleteOne(ReportTemplate reportTemplate) {
		checkNotNull(reportTemplate);
		checkArgument(!reportTemplate.isNew());

		deleteOne(reportTemplate.getId());
	}

    /**
     *
     * @param reportTemplateId
     */
	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public void deleteOne(long reportTemplateId) {

		if (checkCanUpdate(reportTemplateId)) {
			if (reportTemplateBodyRepository.existsById(reportTemplateId)) {
				reportTemplateBodyRepository.deleteById(reportTemplateId);
			}
			reportTemplateRepository.deleteById(reportTemplateId);
		} else {
			throw new PersistenceException(String.format("Can't delete report template (id=%d)", reportTemplateId));
		}

	}

	/**
	 *
	 * @param reportTemplate
	 */
	@Transactional
	@Secured({ AuthoritiesConstants.ADMIN })
	public void deleteOneCommon(ReportTemplate reportTemplate) {
		checkNotNull(reportTemplate);
		checkArgument(!reportTemplate.isNew());

		deleteOneCommon(reportTemplate.getId());
	}

    /**
     *
     * @param reportTemplateId
     */
	@Transactional
	@Secured({ AuthoritiesConstants.ADMIN })
	public void deleteOneCommon(long reportTemplateId) {

		if (checkIsCommon(reportTemplateId)) {
			if (reportTemplateBodyRepository.existsById(reportTemplateId)) {
				reportTemplateBodyRepository.deleteById(reportTemplateId);
			}
			reportTemplateRepository.deleteById(reportTemplateId);
		} else {
			throw new PersistenceException(String.format("Can't delete report template (id=%d)", reportTemplateId));
		}

	}

    /**
     *
     * @param reportType
     * @param isActive
     * @return
     */
	@Transactional( readOnly = true)
	public List<ReportTemplate> selectDefaultReportTemplates(ReportTypeKey reportType, boolean isActive) {
		return reportTemplateRepository.selectCommonTemplates(reportType.getKeyname(), isActive);
	}

	/**
	 *
	 * @param reportType
	 * @param isActive
	 * @param subscriberId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ReportTemplate> selectSubscriberReportTemplates(ReportTypeKey reportType, boolean isActive,
			long subscriberId) {

		List<ReportTemplate> result = reportTemplateRepository.selectSubscriberTemplates(reportType.getKeyname(),
				isActive, subscriberId);

		return  result;
	}

    /**
     *
     * @param reportTypeKey
     * @param isActive
     * @return
     */
	@Transactional( readOnly = true)
	public List<ReportTemplate> selectCommonReportTemplates(ReportTypeKey reportTypeKey, boolean isActive) {

		List<ReportTemplate> result = reportTemplateRepository.selectCommonTemplates(reportTypeKey.getKeyname(),
				isActive);

		return result;
	}

    /**
     *
     * @param subscriberId
     * @param reportType
     * @param isActive
     * @return
     */
	@Transactional( readOnly = true)
	public List<ReportTemplate> getAllReportTemplates(long subscriberId, ReportTypeKey reportType, boolean isActive) {

		List<ReportTemplate> result = new ArrayList<>();
		List<ReportTemplate> commonTemplates = selectDefaultReportTemplates(reportType, isActive);

		List<ReportTemplate> subscriberTemplates = selectSubscriberReportTemplates(reportType, isActive, subscriberId);

		result.addAll(commonTemplates);
		result.addAll(subscriberTemplates);

		return result;
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional( readOnly = true)
	public boolean checkCanUpdate(Long id) {
		return !checkIsCommon(id);
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional( readOnly = true)
	public boolean checkIsCommon(Long id) {
		List<Long> ids = reportTemplateRepository.selectCommonTemplateIds();
		return ids.contains(id);
	}

	/**
	 *
	 * @param reportTemplateId
	 * @param body
	 * @param filename
	 * @param isCompiled
	 */
	private void saveReportTemplateBodyInternal(long reportTemplateId, byte[] body, String filename,
			boolean isCompiled) {
		Optional<ReportTemplateBody> rtbOpt = reportTemplateBodyRepository.findById(reportTemplateId);
        ReportTemplateBody rtb;
		if (!rtbOpt.isPresent()) {
			rtb = new ReportTemplateBody();
			rtb.setReportTemplateId(reportTemplateId);
		} else {
		    rtb = rtbOpt.get();
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
     * @param body
     * @param filename
     */
	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public void saveReportTemplateBody(long reportTemplateId, byte[] body, String filename) {
		saveReportTemplateBodyInternal(reportTemplateId, body, filename, false);
	}

    /**
     *
     * @param reportTemplateId
     * @param body
     * @param filename
     */
	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public void saveReportTemplateBodyCompiled(long reportTemplateId, byte[] body, String filename) {

		saveReportTemplateBodyInternal(reportTemplateId, body, filename, true);
	}

    /**
     *
     * @param srcId
     * @param reportTemplate
     * @param subscriber
     * @return
     */
	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public ReportTemplate createByTemplate(long srcId, ReportTemplate reportTemplate, Subscriber subscriber) {

		checkNotNull(reportTemplate);
		checkArgument(reportTemplate.isNew());

		checkNotNull(subscriber);
		checkArgument(!subscriber.isNew());

		ReportTemplate srcReportTemplate = reportTemplateRepository.findById(srcId)
            .orElseThrow(() -> new EntityNotFoundException(ReportTemplate.class, srcId));

		reportTemplate.setReportTypeKeyname(srcReportTemplate.getReportTypeKeyname());
		reportTemplate.setSubscriber(subscriber);
		reportTemplate.setSrcReportTemplateId(srcId);
		reportTemplate.setIntegratorIncluded(srcReportTemplate.getIntegratorIncluded());
		reportTemplate.set_default(false);
		reportTemplate.set_active(true);
		reportTemplate.setActiveEndDate(null);
		reportTemplate.setActiveStartDate(new Date());

		ReportTemplate result = reportTemplateRepository.save(reportTemplate);

		ReportTemplateBody srcBody = getReportTemplateBody(srcReportTemplate.getId());
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
	@Transactional
	@Secured({ AuthoritiesConstants.SUBSCR_USER, AuthoritiesConstants.SUBSCR_ADMIN })
	public ReportTemplate moveToArchive(long reportTemplateId) {

		if (!checkCanUpdate(reportTemplateId)) {
			throw new PersistenceException(
					String.format("ReportTemplate with (id=%d) is not updatable", reportTemplateId));
		}

		List<ReportParamset> reportParamsetList = reportParamsetRepository.selectReportParamset(reportTemplateId, true);

		if (reportParamsetList.size() > 0) {
			return null;
		}

		ReportTemplate rt = reportTemplateRepository.findById(reportTemplateId)
            .orElseThrow(() -> new EntityNotFoundException(ReportTemplate.class, reportTemplateId));

		if (!rt.is_active()) {
			throw new PersistenceException(
					String.format("ReportTemplate (id=%d) is alredy archived", reportTemplateId));
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
	@Transactional( readOnly = true)
	public ReportTemplateBody getReportTemplateBody(long reportTemplateId) {
		return reportTemplateBodyRepository.findById(reportTemplateId)
            .orElseThrow(() -> new EntityNotFoundException(ReportMasterTemplateBody.class, reportTemplateId));
	}

	/**
	 *
	 * @param reportTypeKey
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ReportTemplate> selectActiveReportTemplates(ReportTypeKey reportTypeKey) {
		return reportTemplateRepository.selectActiveTemplates(reportTypeKey.getKeyname(), true);
	}

	/**
	 *
	 * @param reportTypeKey
	 * @param isActive
	 * @param isCompiled
	 */
	@Transactional
	public int updateCommonReportTemplateBody(ReportTypeKey reportTypeKey, boolean isActive, boolean isCompiled) {
		List<ReportTemplate> updateCadidates = reportTemplateRepository
				.selectCommonTemplates(reportTypeKey.getKeyname(), isActive);

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
			srcBodyFilename = reportMasterTemplateBody.getBodyCompiledFilename();
		} else {
			srcBody = reportMasterTemplateBody.getBody();
			srcBodyFilename = reportMasterTemplateBody.getBodyFilename();
		}

		if (srcBody != null) {
			logger.info("MasterTemplateBody size length: {}", srcBody.length);
		}

		logger.info("MasterTemplateBody file name  : {}", srcBodyFilename);

		//		checkNotNull(srcBody);

		int resultIdx = 0;
		for (ReportTemplate rt : updateCadidates) {
			logger.info("Updating ReportTemplate:{}", rt.getId());

			saveReportTemplateBodyInternal(rt.getId(), srcBody, srcBodyFilename, isCompiled);
			resultIdx++;
		}

		return resultIdx;
	}

	/**
	 *
	 * @param reportTypeKey
	 * @return
	 */
	@Transactional
	@Secured({ AuthoritiesConstants.ADMIN })
	public ReportTemplate createCommonReportTemplate(ReportTypeKey reportTypeKey) {

		checkNotNull(reportTypeKey);

		ReportMasterTemplateBody masterTemplateBody = reportMasterTemplateBodyService
				.selectReportMasterTemplate(reportTypeKey);

		if (masterTemplateBody == null) {
			throw new PersistenceException(
					String.format("ReportMasterTemplateBody for report %s not found", reportTypeKey.name()));
		}

		ReportTemplate reportTemplate = new ReportTemplate();

		reportTemplate.setReportTypeKeyname(reportTypeKey.getKeyname());
		ReportType reportType = reportTypeService.findByKeyname(reportTypeKey.name());
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
		reportTemplateBody.setBodyCompiled(masterTemplateBody.getBodyCompiled());
		reportTemplateBody.setBodyFilename(masterTemplateBody.getBodyFilename());
		reportTemplateBody.setBodyCompiledFilename(masterTemplateBody.getBodyCompiledFilename());

		reportTemplateBodyRepository.save(reportTemplateBody);

		return result;
	}

    /**
     *
     * @param reportTypeKey
     * @param reportTemplateId
     * @param isCompiled
     */
	@Transactional
	public void updateTemplateBodyFromMaster(ReportTypeKey reportTypeKey, long reportTemplateId, boolean isCompiled) {

		ReportTemplate reportTemplate = reportTemplateRepository.findById(reportTemplateId)
            .orElseThrow(() -> new EntityNotFoundException(ReportTemplate.class, reportTemplateId));

		ReportMasterTemplateBody reportMasterTemplateBody = reportMasterTemplateBodyService
				.selectReportMasterTemplate(reportTypeKey);

		byte[] srcBody;
		String srcBodyFilename;
		if (isCompiled) {
			srcBody = reportMasterTemplateBody.getBodyCompiled();
			srcBodyFilename = reportMasterTemplateBody.getBodyCompiledFilename();
		} else {
			srcBody = reportMasterTemplateBody.getBody();
			srcBodyFilename = reportMasterTemplateBody.getBodyFilename();
		}
		logger.info("MasterTemplateBody size length: {}", srcBody.length);
		logger.info("MasterTemplateBody file name  : {}", srcBodyFilename);

		checkNotNull(srcBody);

		logger.info("Updating ReportTemplate:{}", reportTemplate);

		saveReportTemplateBodyInternal(reportTemplate.getId(), srcBody, srcBodyFilename, isCompiled);

	}

}
