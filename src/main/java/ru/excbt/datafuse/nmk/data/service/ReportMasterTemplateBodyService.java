package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;
import ru.excbt.datafuse.nmk.data.repository.ReportMasterTemplateBodyRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.utils.ResourceHelper;

@Service
@Transactional
public class ReportMasterTemplateBodyService implements SecuredRoles {

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

	/**
	 * 
	 * @param fileResource
	 * @return
	 * @throws IOException
	 */
	@Transactional
	@Secured({ ROLE_ADMIN })
	public boolean saveReportMasterTemplateBody(
			long reportMasterTemplateBodyId, String fileResource,
			boolean isCompiled) throws IOException {
		File file = ResourceHelper.findResource(fileResource);

		if (!file.exists()) {
			return false;
		}

		byte[] fileBytes = null;
		InputStream is = new FileInputStream(file);
		try {
			fileBytes = IOUtils.toByteArray(is);
		} finally {
			is.close();
		}

		checkNotNull(fileBytes);

		ReportMasterTemplateBody entity = reportMasterTemplateBodyRepository
				.findOne(reportMasterTemplateBodyId);
		if (entity == null) {
			return false;
		}

		if (isCompiled) {
			entity.setBodyCompiled(fileBytes);
			entity.setBodyCompiledFilename(file.getName());

		} else {
			entity.setBody(fileBytes);
			entity.setBodyFilename(file.getName());
		}
		reportMasterTemplateBodyRepository.save(entity);

		return true;
	}

	@Transactional
	@Secured({ ROLE_ADMIN })
	public ReportMasterTemplateBody createOne(ReportTypeKey reportTypeKey) {
		ReportMasterTemplateBody entity = new ReportMasterTemplateBody();
		entity.setReportTypeKey(reportTypeKey);

		return reportMasterTemplateBodyRepository.save(entity);
	}

}