package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;
import ru.excbt.datafuse.nmk.data.repository.ReportMasterTemplateBodyRepository;

@Service
@Transactional
public class ReportMasterTemplateBodyService {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportMasterTemplateBodyService.class);

	@Autowired
	private ReportMasterTemplateBodyRepository reportMasterTemplateBodyRepository;

	/**
	 * 
	 * @param reportTypeKey
	 * @return
	 */
	@Transactional(readOnly = true)
	public ReportMasterTemplateBody selectReportMasterTemplate(
			ReportTypeKey reportTypeKey) {
		List<ReportMasterTemplateBody> resultList = reportMasterTemplateBodyRepository
				.findByReportTypeKey(reportTypeKey);

		if (resultList.size() == 0) {
			logger.error("ATTENTION! No found Master Template of type {} ",
					reportTypeKey);

			return null;
		}

		if (resultList.size() > 1) {
			logger.warn(
					"ATTENTION! More htan 1 Master Template of type {}. Take first one with id:{}",
					reportTypeKey, resultList.get(0).getId());
		}

		return resultList.get(0);
	}
}
