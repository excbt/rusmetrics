package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;
import ru.excbt.datafuse.nmk.data.repository.ReportMasterTemplateBodyRepository;
import ru.excbt.datafuse.nmk.report.ReportConstants;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

/**
 * Сервис для работы с главным шаблоном отчетов
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.04.2015
 *
 */
@Service
public class ReportMasterTemplateBodyService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(ReportMasterTemplateBodyService.class);

	@Autowired
	private ReportMasterTemplateBodyRepository reportMasterTemplateBodyRepository;

	/**
	 *
	 * @param reportTypeKey
	 * @return
	 */
	@Transactional(readOnly = true)
	public ReportMasterTemplateBody selectReportMasterTemplate(ReportTypeKey reportTypeKey) {
		List<ReportMasterTemplateBody> resultList = reportMasterTemplateBodyRepository
				.findByReportTypeKeyname(reportTypeKey.getKeyname());

		if (resultList.size() == 0) {
			logger.error("ATTENTION! No found Master Template of type {} ", reportTypeKey);

			return null;
		}

		if (resultList.size() > 1) {
			logger.warn("ATTENTION! More htan 1 Master Template of type {}. Take first one with id:{}", reportTypeKey,
					resultList.get(0).getId());
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
	public boolean saveJasperReportMasterTemplateBody(long reportMasterTemplateBodyId, String fileResource,
			boolean isCompiled) throws IOException {

		String correctedFilename = FilenameUtils.removeExtension(fileResource)
				+ (isCompiled ? ReportConstants.EXT_JASPER : ReportConstants.EXT_JRXML);

		File file = new File(correctedFilename);

		if (!file.exists()) {
			throw new FileNotFoundException(correctedFilename);
		}

		byte[] fileBytes = null;
		InputStream is = new FileInputStream(file);
		try {
			fileBytes = IOUtils.toByteArray(is);
		} finally {
			is.close();
		}

		checkNotNull(fileBytes);

		Optional<ReportMasterTemplateBody> entityOpt = reportMasterTemplateBodyRepository.findById(reportMasterTemplateBodyId);
		if (!entityOpt.isPresent()) {
			return false;
		}

		ReportMasterTemplateBody entity = entityOpt.get();

		logger.info("New File {} size {}", file.getAbsolutePath(), fileBytes.length);
		byte[] bb = entity.getBodyCompiled();
		logger.info("Current Report Template Body size {}", bb != null ? bb.length : 0);

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

	/**
	 *
	 * @param reportMasterTemplateBodyId
	 * @param fileResource
	 * @param isCompiled
	 * @return
	 * @throws IOException
	 */
	public boolean savePentahoReportMasterTemplateBody(long reportMasterTemplateBodyId, String fileResource,
			boolean isCompiled) throws IOException {

		String correctedFilename = FilenameUtils.removeExtension(fileResource) + ReportConstants.EXT_PRPT;

		File file = new File(correctedFilename);

		if (!file.exists()) {
			throw new FileNotFoundException(correctedFilename);
		}

		byte[] fileBytes = null;
		InputStream is = new FileInputStream(file);
		try {
			fileBytes = IOUtils.toByteArray(is);
		} finally {
			is.close();
		}

		checkNotNull(fileBytes);

		Optional<ReportMasterTemplateBody> entityOpt = reportMasterTemplateBodyRepository.findById(reportMasterTemplateBodyId);
		if (!entityOpt.isPresent()) {
			return false;
		}

		ReportMasterTemplateBody entity = entityOpt.get();

		logger.info("New File {} size {}", file.getAbsolutePath(), fileBytes.length);
		byte[] bb = entity.getBodyCompiled();
		logger.info("Current Report Template Body size {}", bb != null ? bb.length : 0);

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

    /**
     *
     * @param reportMasterTemplateBodyId
     * @param fileResource
     * @param isCompiled
     * @return
     * @throws IOException
     */
    public boolean saveExcelReportMasterTemplateBody(long reportMasterTemplateBodyId, String fileResource,
                                                       boolean isCompiled) throws IOException {

        String correctedFilename = FilenameUtils.removeExtension(fileResource) + ReportConstants.EXT_XLSX;

        File file = new File(correctedFilename);

        if (!file.exists()) {
            throw new FileNotFoundException(correctedFilename);
        }

        byte[] fileBytes = null;
        InputStream is = new FileInputStream(file);
        try {
            fileBytes = IOUtils.toByteArray(is);
        } finally {
            is.close();
        }

        checkNotNull(fileBytes);

        Optional<ReportMasterTemplateBody> entityOpt = reportMasterTemplateBodyRepository.findById(reportMasterTemplateBodyId);
        if (!entityOpt.isPresent()) {
            return false;
        }

        ReportMasterTemplateBody entity = entityOpt.get();

        logger.info("New File {} size {}", file.getAbsolutePath(), fileBytes.length);
        byte[] bb = entity.getBodyCompiled();
        logger.info("Current Report Template Body size {}", bb != null ? bb.length : 0);

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

    /**
	 *
	 * @param reportTypeKey
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_ADMIN })
	public ReportMasterTemplateBody createOne(ReportTypeKey reportTypeKey) {
		ReportMasterTemplateBody entity = new ReportMasterTemplateBody();
		entity.setReportTypeKeyname(reportTypeKey.getKeyname());

		return reportMasterTemplateBodyRepository.save(entity);
	}

}
