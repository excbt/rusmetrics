package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JRException;
import ru.excbt.datafuse.nmk.config.jpa.JasperDatabaseConnectionSettings;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportTemplateBody;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.report.ReportConstants;
import ru.excbt.datafuse.nmk.report.ReportOutputFileType;
import ru.excbt.datafuse.nmk.report.ReportPeriodKey;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.nmk.reports.NmkReport;
import ru.excbt.nmk.reports.NmkReport.FileType;
import ru.excbt.nmk.reports.NmkReport.ReportType;

@Service
@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
public class ReportService {

	public final static String DATE_TEMPLATE = "yyyy-MM-dd";
	public final static boolean IS_ZIP_TRUE = true;
	public final static boolean IS_ZIP_FALSE = !IS_ZIP_TRUE;
	public final static Charset UTF8_CHARSET = Charset.forName("UTF-8");

	private final static Map<ReportOutputFileType, FileType> NMK_REPORTS_TYPE_CONVERTER;

	static {
		Map<ReportOutputFileType, FileType> typeMap = new HashMap<>();
		typeMap.put(ReportOutputFileType.HTML, FileType.HTML);
		typeMap.put(ReportOutputFileType.PDF, FileType.PDF);
		typeMap.put(ReportOutputFileType.XLSX, FileType.XLSX);
		typeMap.put(ReportOutputFileType.XLS, FileType.XLSX);

		NMK_REPORTS_TYPE_CONVERTER = Collections.unmodifiableMap(typeMap);
	}

	private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

	@Autowired
	private SystemParamService systemParamService;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private ReportMakerParamService reportMakerParamService;

	@Autowired
	private JasperDatabaseConnectionSettings jasperConfig;

	@Autowired
	private SubscriberService subscriberService;

	@PersistenceContext(unitName = "nmk-p")
	private EntityManager em;

	/**
	 * 
	 * @param contObjectId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	protected String getCommercialReportPathHtml(long contObjectId, DateTime beginDate, DateTime endDate) {
		return getCommercialReportPath(ReportOutputFileType.HTML, contObjectId, beginDate, endDate);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	protected String getCommercialReportPathPdf(long contObjectId, DateTime beginDate, DateTime endDate) {
		return getCommercialReportPath(ReportOutputFileType.PDF, contObjectId, beginDate, endDate);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	protected String getCommercialReportPath(ReportOutputFileType reportType, long contObjectId, DateTime beginDate,
			DateTime endDate) {
		checkNotNull(reportType);
		checkArgument(contObjectId > 0);
		checkNotNull(beginDate);
		checkNotNull(endDate);
		checkArgument(beginDate.compareTo(endDate) <= 0, "beginDate is bigger than endDate");

		String defaultUrl = systemParamService.getParamValueAsString(ReportConstants.COMMERCIAL_REPORT_TEMPLATE_PATH);

		checkNotNull(defaultUrl);

		return String.format(defaultUrl, reportType.toLowerName(), endDate.toString(DATE_TEMPLATE),
				beginDate.toString(DATE_TEMPLATE), contObjectId);
	}

	/**
	 * 
	 * @return
	 */
	public boolean externalJasperServerEnable() {
		boolean result = false;
		try {
			result = systemParamService.getParamValueAsBoolean(ReportConstants.EXTERNAL_JASPER_SERVER_ENABLE);
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
		String result = systemParamService.getParamValueAsString(ReportConstants.EXTERNAL_JASPER_SERVER_URL);
		return result;
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	private InputStream getReportParamsetTemplateBody(long reportParamsetId) {
		ReportTemplateBody reportTemplateBody = reportTemplateService.getReportTemplateBody(reportParamsetId);

		checkNotNull(reportTemplateBody.getBodyCompiled());
		checkState(reportTemplateBody.getBodyCompiled().length > 0);
		ByteArrayInputStream is = new ByteArrayInputStream(reportTemplateBody.getBodyCompiled());

		return is;
	}

	/**
	 * 
	 * @param outputStream
	 * @param reportParamsetId
	 */
	private ReportParamset makeReportByParamsetId(long reportParamsetId, LocalDateTime reportDate,
			OutputStream outputStream) {

		checkNotNull(outputStream);
		ReportParamset reportParamset = reportParamsetService.findOne(reportParamsetId);

		if (reportParamset == null) {
			throw new PersistenceException(String.format("ReportParamset (id=%d) not found", reportParamsetId));
		}

		// ReportMakerParam reportMakerParam =
		// getReportMakerParam(reportParamsetId);

		// return makeReportByParamset(reportMakerParam, reportDate,
		// outputStream);

		return null;
	}

	/**
	 * 
	 * @param reportParamset
	 * @param subscriberId
	 * @param reportDate
	 * @param outputStream
	 * @param isZip
	 * @return
	 */
	public ReportParamset makeReportByParamset(ReportMakerParam reportMakerParam, LocalDateTime reportDate,
			OutputStream outputStream) {

		checkNotNull(outputStream);
		checkState(reportMakerParam.isSubscriberValid());

		ReportParamset reportParamset = reportMakerParam.getReportParamset();

		final boolean isZippedStream = reportMakerParam.isOutputFileZipped();

		InputStream is = getReportParamsetTemplateBody(reportParamset.getReportTemplateId());

		OutputStream outputStreamWrapper = null;
		if (isZippedStream) {
			outputStreamWrapper = new ZipOutputStream(outputStream, UTF8_CHARSET);
		} else {
			outputStreamWrapper = outputStream;
		}

		try {
			makeJasperReport(reportMakerParam, reportDate, is, outputStreamWrapper, isZippedStream);
		} finally {
			if (isZippedStream) {
				try {
					outputStreamWrapper.flush();
					outputStreamWrapper.close();
				} catch (IOException e) {
					logger.error("Error during close ZIP output stream: {}", e);
					reportParamset = null;
				}
			}
		}

		return reportParamset;
	}

	/**
	 * 
	 * @param outputStream
	 * @param reportParamsetId
	 */
	private void makeJasperReport(ReportMakerParam reportMakerParam, LocalDateTime reportDate, InputStream inputStream,
			OutputStream outputStream, boolean isZip) {

		checkNotNull(inputStream);
		checkNotNull(outputStream);
		checkNotNull(reportMakerParam);
		checkState(reportMakerParam.isParamsetValid());

		final ReportParamset reportParamset = reportMakerParam.getReportParamset();

		LocalDateTime dtStart = null;
		LocalDateTime dtEnd = null;
		if (reportParamset.getReportPeriodKey() == ReportPeriodKey.INTERVAL) {
			if (reportParamset.getParamsetStartDate() == null || reportParamset.getParamsetEndDate() == null) {
				throw new IllegalArgumentException(String.format(
						"ReportParamset (id=%d) is invalid. "
								+ "ParamsetStartDate and ParamsetEndDate is not set correctly. " + "ReportPeriodKey=%s",
						reportParamset.getId(), ReportPeriodKey.INTERVAL));
			}
			dtStart = new LocalDateTime(reportParamset.getParamsetStartDate());
			dtEnd = new LocalDateTime(reportParamset.getParamsetEndDate());

		} else {
			dtStart = ReportParamsetUtils.getStartDateTime(reportDate, reportParamset.getReportPeriodKey());
			dtEnd = ReportParamsetUtils.getEndDateTime(reportDate, reportParamset.getReportPeriodKey());
		}

		List<Long> makeObjectIds = reportMakerParam.getContObjectList();

		long[] objectIds = ArrayUtils.toPrimitive(makeObjectIds.toArray(new Long[0]));

		checkNotNull(objectIds, "ContObject for report is not set");

		NmkReport rep = null;
		try {

			rep = new NmkReport(jasperConfig.getDatasourceUrl(), jasperConfig.getDatasourceUsername(),
					jasperConfig.getDatasourcePassword());

			String keyname = reportParamset.getReportTemplate().getReportTypeKeyname();

			ReportTypeKey rptKey = ReportTypeKey.valueOf(keyname);

			ReportType destReportType = reportTypeConverter(rptKey);

			FileType convertedFileType = NMK_REPORTS_TYPE_CONVERTER.get(reportMakerParam.reportOutputFileType());

			if (convertedFileType == null) {
				logger.warn(
						"NMK_REPORTS_TYPE_CONVERTER for ReportMakerParam's reportOutputFileType {}  is null. Using default file type {}",
						reportMakerParam.reportOutputFileType(), FileType.PDF);
				convertedFileType = FileType.PDF;
			}

			// TODO

			Map<String, Object> paramSpecialMap = reportMakerParamService.getParamSpecialValues(reportMakerParam);

			long idParam = 0;
			if (reportMakerParam.isSpecialIdParam()) {
				idParam = reportMakerParam.getIdParam();
			} else {
				idParam = reportMakerParam.getIdParam();
			}

			logger.debug(
					"Call nmkGetReport with params (reportType:{}; (is, os); "
							+ "idParam:{}; startDate:{}; endDate:{}; objectIds:{}; "
							+ "convertedFileType:{}, isZip: {}, paramSpecialMap...)",
					destReportType, idParam, dtStart.toDate(), dtEnd.toDate(), Arrays.toString(objectIds),
					convertedFileType, isZip);

			rep.nmkGetReport(destReportType, inputStream, outputStream, idParam, dtStart.toDate(), dtEnd.toDate(),
					objectIds, convertedFileType, isZip, paramSpecialMap);

		} catch (JRException | IOException e) {
			logger.error("NmkReport exception: {}", e);
			throw new PersistenceException(String.format("NmkReport exception: %s", e.getMessage()));
		} catch (ClassNotFoundException e) {
			logger.error("NmkReport exception: {}", e);
			throw new IllegalStateException("Can't initialize NmkReport");
		} catch (SQLException e) {
			logger.error("NmkReport exception: {}", e);
			throw new PersistenceException(String.format("NmkReport exception:", e.getMessage()));
		} finally {
			if (rep != null) {
				try {
					rep.close();
				} catch (SQLException e) {
					logger.error("NmkReport exception: {}", e);
				}
			}
		}
	}

	/**
	 * 
	 * @param reportTypeKey
	 * @return
	 */
	private ReportType reportTypeConverter(ReportTypeKey reportTypeKey) {
		ReportType result = null;
		switch (reportTypeKey) {
		case COMMERCE_REPORT: {
			result = ReportType.RPT_COMMERCE;
			break;
		}
		case CONS_T1_REPORT: {
			result = ReportType.RPT_CONSOLIDATED_1;
			break;
		}
		case CONS_T2_REPORT: {
			result = ReportType.RPT_CONSOLIDATED_2;
			break;
		}
		case EVENT_REPORT: {
			result = ReportType.RPT_ALERTS;
			break;
		}
		case METROLOGICAL_REPORT: {
			result = ReportType.RPT_METROLOGICAL;
			break;
		}
		case CONSUMPTION_REPORT: {
			result = ReportType.RPT_CONSUMPTION;
			break;
		}
		case CONSUMPTION_HISTORY_REPORT: {
			result = ReportType.RPT_CONSUMPTION_HISTORY;
			break;
		}
		case CONSUMPTION_ETALON_REPORT: {
			result = ReportType.RPT_CONSUMPTION_ETALON;
			break;
		}
		case LOG_JOURNAL_REPORT: {
			result = ReportType.RPT_LOG_JOURNAL;
			break;
		}
		case PARTNER_SERVICE_REPORT: {
			result = ReportType.RPT_PARTNER_SERVICE;
			break;
		}
		case ABONENT_SERVICE_REPORT: {
			result = ReportType.RPT_ABONENT_SERVICE;
			break;
		}
		default: {
			break;
		}
		}
		checkNotNull(result);
		return result;
	}

	/**
	 * 
	 * @throws SQLException
	 */
	@Transactional(readOnly = true)
	public void testConnection() throws SQLException {

		checkState(em.isOpen());

		Session sess = em.unwrap(Session.class);

		sess.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				// TODO testConnection Block
				logger.info("Connection: {}", connection.toString());
			}
		});

		checkNotNull(sess);

	}

}
