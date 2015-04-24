package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Arrays;
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

		ReportTemplateBody reportTemplateBody = reportTemplateService
				.getReportTemplateBody(reportParamset.getReportTemplate()
						.getId());

		checkNotNull(reportTemplateBody.getBodyCompiled());
		checkState(reportTemplateBody.getBodyCompiled().length > 0);
		ByteArrayInputStream is = new ByteArrayInputStream(
				reportTemplateBody.getBodyCompiled());

		makeCommerceReportZip(reportParamset, reportDate, is, outputStream);
	}

	/**
	 * 
	 * @param outputStream
	 * @param reportParamsetId
	 */
	public void makeCommerceReportZip(ReportParamset reportParamset,
			LocalDateTime reportDate, InputStream inputStream,
			OutputStream outputStream) {

		checkNotNull(inputStream);
		checkNotNull(outputStream);
		checkNotNull(reportParamset);
		checkArgument(!reportParamset.isNew());
		checkNotNull(reportParamset.getReportPeriodKey());

		List<Long> reportParamsetObjectIds = reportParamsetService
				.selectReportParamsetObjectIds(reportParamset.getId());



		LocalDateTime dtStart = null;
		LocalDateTime dtEnd = null;
		if (reportParamset.getReportPeriodKey() == ReportPeriodKey.INTERVAL) {
			if (reportParamset.getParamsetStartDate() == null
					|| reportParamset.getParamsetEndDate() == null) {
				throw new IllegalArgumentException(
						String.format(
								"ReportParamset (id=%d) is invalid. "
										+ "ParamsetStartDate and ParamsetEndDat is not set correctly. "
										+ "ReportPeriodKey=%s",
								reportParamset.getId(),
								ReportPeriodKey.INTERVAL));
			}
			dtStart = new LocalDateTime(reportParamset.getParamsetStartDate());
			dtEnd = new LocalDateTime(reportParamset.getParamsetEndDate());

		} else {
			dtStart = ReportParamsetUtils.getStartDate(reportDate,
					reportParamset.getReportPeriodKey());
			dtEnd = ReportParamsetUtils.getEndDate(reportDate,
					reportParamset.getReportPeriodKey());
		}

		long[] objectIds = ArrayUtils.toPrimitive(reportParamsetObjectIds
				.toArray(new Long[0]));

		NmkReport rep = null;
		ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
		try {

			rep = new NmkReport(jpaConfig.getDatasourceUrl(),
					jpaConfig.getDatasourceUsername(),
					jpaConfig.getDatasourcePassword());

			logger.info(
					"Call nmkGetReport with params (startDate:{};endDate:{};idParam:0;objectIds:{};)",
					dtStart, dtEnd, Arrays.toString(objectIds));

			rep.nmkGetReport(ReportType.RPT_COMMERCE, inputStream,
					zipOutputStream, 0, dtStart.toDate(), dtEnd.toDate(),
					objectIds, FileType.PDF);

		} catch (JRException | IOException e) {
			logger.error("NmkReport exception: {}", e);
			throw new PersistenceException(String.format(
					"NmkReport exception: %s", e.getMessage()));
		} catch (ClassNotFoundException e) {
			logger.error("NmkReport exception: {}", e);
			throw new IllegalStateException("Can't initialize NmkReport");
		} catch (SQLException e) {
			logger.error("NmkReport exception: {}", e);
			throw new PersistenceException(String.format(
					"NmkReport exception:", e.getMessage()));
		} finally {
			try {
				zipOutputStream.flush();
				zipOutputStream.close();
			} catch (IOException e) {
				logger.error("NmkReport exception: {}", e);
			}
			
			if (rep != null) {
				try {
					rep.close();
				} catch (SQLException e) {
					logger.error("NmkReport exception: {}", e);
				}
			}
		}

	}
}
