package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.persistence.PersistenceException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.config.jpa.JpaConfig;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportOutputFileType;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportPeriodKey;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportTemplateBody;
import ru.excbt.nmk.reports.NmkReport;
import ru.excbt.nmk.reports.NmkReport.FileType;
import ru.excbt.nmk.reports.NmkReport.ReportType;

@Service
public class ReportService {

	public final static String DATE_TEMPLATE = "yyyy-MM-dd";

	private static final Logger logger = LoggerFactory
			.getLogger(ReportService.class);

	@Autowired
	private SystemParamService systemParamService;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private JpaConfig jpaConfig;

	/**
	 * 
	 * @param contObjectId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public String getCommercialReportPathHtml(long contObjectId,
			DateTime beginDate, DateTime endDate) {
		return getCommercialReportPath(ReportOutputFileType.HTML, contObjectId,
				beginDate, endDate);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public String getCommercialReportPathPdf(long contObjectId,
			DateTime beginDate, DateTime endDate) {
		return getCommercialReportPath(ReportOutputFileType.PDF, contObjectId,
				beginDate, endDate);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public String getCommercialReportPath(ReportOutputFileType reportType,
			long contObjectId, DateTime beginDate, DateTime endDate) {
		checkNotNull(reportType);
		checkArgument(contObjectId > 0);
		checkNotNull(beginDate);
		checkNotNull(endDate);
		checkArgument(beginDate.compareTo(endDate) <= 0,
				"beginDate is bigger than endDate");

		String defaultUrl = systemParamService
				.getParamValueAsString(ReportConstants.COMMERCIAL_REPORT_TEMPLATE_PATH);

		checkNotNull(defaultUrl);

		return String.format(defaultUrl, reportType.toLowerName(),
				endDate.toString(DATE_TEMPLATE),
				beginDate.toString(DATE_TEMPLATE), contObjectId);
	}

	/**
	 * 
	 * @return
	 */
	public boolean externalJasperServerEnable() {
		boolean result = false;
		try {
			result = systemParamService
					.getParamValueAsBoolean(ReportConstants.EXTERNAL_JASPER_SERVER_ENABLE);
		} catch (PersistenceException e) {
			logger.error(e.toString());
			result = false;
		}

		return result;
	}

	/**
	 * 
	 * @return
	 */
	public String externalJasperServerUrl() {
		String result = systemParamService
				.getParamValueAsString(ReportConstants.EXTERNAL_JASPER_SERVER_URL);
		return result;
	}

	/**
	 * 
	 * @param outputStream
	 * @param reportParamsetId
	 */
	public void makeCommerceReportZip(long reportParamsetId,
			LocalDateTime reportDate, OutputStream outputStream) {

		checkNotNull(outputStream);

		ReportParamset reportParamset = reportParamsetService
				.findOne(reportParamsetId);

		if (reportParamset == null) {
			throw new PersistenceException(String.format(
					"ReportParamset (id=%d) not found", reportParamsetId));
		}

		if (reportParamset.getReportPeriodKey() == null) {
			throw new PersistenceException(
					String.format(
							"ReportParamset (id=%d) is invalid. ReportPeriodKey is not set",
							reportParamsetId));
		}

		List<Long> reportParamsetObjectIds = reportParamsetService
				.selectReportParamsetObjectIds(reportParamsetId);

		ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

		NmkReport rep = null;
		try {
			rep = new NmkReport(jpaConfig.getDatasourceUrl(),
					jpaConfig.getDatasourceUsername(),
					jpaConfig.getDatasourcePassword());
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Can't initialize NmkReport");
		} catch (SQLException e) {
			throw new PersistenceException(String.format(
					"NmkReport connection to %s cannot be established",
					jpaConfig.getDatasourceUrl()));
		}

		LocalDateTime dtStart = null;
		LocalDateTime dtEnd = null;
		if (reportParamset.getReportPeriodKey() == ReportPeriodKey.INTERVAL) {
			if (reportParamset.getParamsetStartDate() == null
					|| reportParamset.getParamsetEndDate() == null) {
				throw new IllegalStateException(
						String.format(
								"ReportParamset (id=%d) is invalid. "
										+ "ParamsetStartDate and ParamsetEndDat is not set correctly. "
										+ "ReportPeriodKey=%s",
								reportParamsetId, ReportPeriodKey.INTERVAL));
			}
			dtStart = new LocalDateTime(reportParamset.getParamsetStartDate());
			dtEnd = new LocalDateTime(reportParamset.getParamsetEndDate());

		} else {
			dtStart = ReportParamsetUtils.getStartDate(reportDate,
					reportParamset.getReportPeriodKey());
			dtEnd = ReportParamsetUtils.getEndDate(reportDate,
					reportParamset.getReportPeriodKey());
		}

		ReportTemplateBody reportTemplateBody = reportTemplateService
				.getReportTemplateBody(reportParamset.getReportTemplate()
						.getId());

		checkNotNull(reportTemplateBody.getBodyCompiled());
		checkState(reportTemplateBody.getBodyCompiled().length > 0);
		ByteArrayInputStream is = new ByteArrayInputStream(
				reportTemplateBody.getBodyCompiled());

		long[] objectIds = ArrayUtils.toPrimitive(reportParamsetObjectIds
				.toArray(new Long[0]));

		try {
			rep.nmkGetReport(ReportType.RPT_COMMERCE, is, zipOutputStream, 0,
					dtStart.toDate(), dtEnd.toDate(), objectIds, FileType.PDF);
		} catch (JRException | IOException | SQLException e) {
			logger.error("NmkReport exception: {}", e);
			throw new PersistenceException(String.format(
					"NmkReport exception: %s", e.getMessage()));
		} finally {
			if (rep != null) {
				try {
					rep.close();
				} catch (SQLException e) {
					throw new PersistenceException("Can't close connection");
				}
			}

		}

	}
}
