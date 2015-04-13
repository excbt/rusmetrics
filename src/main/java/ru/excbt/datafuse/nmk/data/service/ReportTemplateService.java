package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.PersistenceException;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKeys;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.model.ReportTemplateBody;
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

	private static final Comparator<ReportTemplate> REPORT_TEMPLATE_COMPARATOR = new Comparator<ReportTemplate>() {

		@Override
		public int compare(ReportTemplate o1, ReportTemplate o2) {
			if (o1.getSubscriber() == null && o1.getSubscriber() != null) {
				return 1;
			}

			if (o1.getSubscriber() != null && o1.getSubscriber() == null) {
				return -1;
			}

			if (o1.getName() == null) {
				return 1;
			}

			if (o2.getName() == null) {
				return -1;
			}

			return o1.getName().compareToIgnoreCase(o2.getName());
		}

	};

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
		if (checkCanUpdateReportTemplate(reportTemplate.getId())) {
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

		if (checkCanUpdateReportTemplate(reportTemplate.getId())) {
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
	public List<ReportTemplate> getDefaultReportTemplates(
			ReportTypeKeys reportType, DateTime currentDate) {
		return reportTemplateRepository
				.selectDefaultTemplates(reportType, true);
	}

	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<ReportTemplate> getSubscriberReportTemplates(long subscriberId,
			ReportTypeKeys reportType, DateTime currentDate) {

		List<ReportTemplate> result = reportTemplateRepository
				.selectSubscriberTemplates(subscriberId, reportType, true);

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
			ReportTypeKeys reportType, DateTime currentDate) {

		List<ReportTemplate> result = new ArrayList<>();
		List<ReportTemplate> defaultTemplate = getDefaultReportTemplates(
				reportType, currentDate);

		List<ReportTemplate> subscriberTemplate = getSubscriberReportTemplates(
				subscriberId, reportType, currentDate);

		result.addAll(defaultTemplate);
		result.addAll(subscriberTemplate);

		return result;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public boolean checkCanUpdateReportTemplate(long id) {
		List<Long> ids = reportTemplateRepository.selectDefaultTemplateIds();
		return ids.indexOf(id) == -1;
	}

	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void saveReportTemplateBody(long reportTemplateId,
			byte[] reportTemplateBody) {

		ReportTemplateBody rtb = reportTemplateBodyRepository
				.findOne(reportTemplateId);
		if (rtb == null) {
			rtb = new ReportTemplateBody();
			rtb.setReportTemplateId(reportTemplateId);
		}
		rtb.setReportTemplateBody(reportTemplateBody);
		reportTemplateBodyRepository.save(rtb);
	}

}
