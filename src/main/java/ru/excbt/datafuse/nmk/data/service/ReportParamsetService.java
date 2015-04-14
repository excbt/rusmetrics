package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.repository.ReportParamsetRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class ReportParamsetService implements SecuredRoles {

	@Autowired
	private ReportParamsetRepository reportParamsetRepository;

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
		reportParamsetRepository.delete(entity);
	}

	/**
	 * 
	 * @param id
	 */
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void deleteOne(long id) {
		reportParamsetRepository.delete(id);
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
}
