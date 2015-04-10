package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.repository.ReportTemplateRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class ReportTemplateService implements SecuredRoles {

	@Autowired
	private ReportTemplateRepository reportTemplateRepository;
	
	
	/**
	 * 
	 * @param reportTemplate
	 * @return
	 */
	@Secured({ROLE_ADMIN, SUBSCR_ROLE_ADMIN})
	public ReportTemplate createOne(ReportTemplate reportTemplate) {
		checkNotNull(reportTemplate);
		checkArgument(reportTemplate.isNew());

		ReportTemplate result = reportTemplateRepository.save(reportTemplate);
		
		return result;
	}
	
	/**
	 * 
	 * @param reportTemplate
	 */
	public void deleteOne(ReportTemplate reportTemplate) {
		checkNotNull(reportTemplate);
		checkArgument(!reportTemplate.isNew());
		reportTemplateRepository.delete(reportTemplate);
		
	}
}
