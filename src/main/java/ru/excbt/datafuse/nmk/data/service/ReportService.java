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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.joda.time.DateTime;
//import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.JasperDatabaseConnectionSettings;

import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportTemplateBody;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.report.ReportConstants;
import ru.excbt.datafuse.nmk.report.ReportOutputFileType;
import ru.excbt.datafuse.nmk.report.ReportPeriodKey;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.report.ReportTypeKeynameConverter;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;
import ru.excbt.nmk.reports.NmkReport;
import ru.excbt.nmk.reports.NmkReport.FileType;
import ru.excbt.nmk.reports.NmkReport.ReportType;

/**
 * Сервис для работы с отчетами
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.03.2015
 *
 */
@Service
@Transactional( readOnly = true)
public class ReportService {

	public final static String DATE_TEMPLATE = "yyyy-MM-dd";
	public final static boolean IS_ZIP_TRUE = true;
	public final static boolean IS_ZIP_FALSE = !IS_ZIP_TRUE;
	public final static Charset UTF8_CHARSET = Charset.forName("UTF-8");

	private final static Map<ReportOutputFileType, FileType> NMK_REPORTS_TYPE_CONVERTER;

	static {
		/**
		 * Инициализация типов файлов
		 */
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
	private ReportMakerParamService reportMakerParamService;

	@Autowired
	private JasperDatabaseConnectionSettings jasperConfig;

	@PersistenceContext
	private EntityManager em;

	public final ReportServiceProps serviceProps = new ReportServiceProps();

	public final class ReportServiceProps {
		private ReportServiceProps() {
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

	}

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
		ZipArchiveOutputStream zipStream = null;
		if (isZippedStream) {
			zipStream = new ZipArchiveOutputStream(outputStream);
			zipStream.setEncoding("Cp866");
			outputStreamWrapper = zipStream;
			//new ZipOutputStream(outputStream, UTF8_CHARSET);
		} else {
			outputStreamWrapper = outputStream;
		}

		try {
			processNmkReport(reportMakerParam, reportDate, is, outputStreamWrapper, isZippedStream);
		} finally {
			if (isZippedStream) {
				try {
					outputStreamWrapper.flush();
					outputStreamWrapper.close();
				} catch (IOException e) {
					logger.error("Error during close ZIP output stream. ReportMakerParam: {}",
							reportMakerParam.toString());
					logger.error("Exception: {}", e);
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
	private void processNmkReport(ReportMakerParam reportMakerParam, LocalDateTime reportDate, InputStream inputStream,
			OutputStream outputStream, boolean isZip) {

		checkNotNull(inputStream);
		checkNotNull(outputStream);
		checkNotNull(reportMakerParam);
		checkState(reportMakerParam.isParamsetValid());

		final ReportParamset reportParamset = reportMakerParam.getReportParamset();

		final LocalDatePeriod reportDatePeriod = getReportPeriod(reportParamset, reportDate);

		// List<Long> makeObjectIds = reportMakerParam.getContObjectList();
		List<Long> reportContObjectObjectIdList = reportMakerParam.getReportContObjectIdList();

		long[] objectIds = ArrayUtils.toPrimitive(reportContObjectObjectIdList.toArray(new Long[0]));

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
            idParam = reportMakerParam.getIdParam();

			Supplier<String> keyValueStringer = () -> {
				StringBuilder keyValue = new StringBuilder();
				paramSpecialMap.forEach((k, v) -> {
					keyValue.append("keyname:").append(k).append('=').append(v.toString()).append("(")
							.append(v.getClass().getName()).append(")").append(";");
				});
				return keyValue.toString();
			};

			logger.debug(
					"Call nmkGetReport with params (reportType:{}; (is, os); "
							+ "idParam:{}; startDate:{}; endDate:{}; objectIds:{}; "
							+ "convertedFileType:{}, isZip: {}, paramSpecialMap: {})",
					destReportType, idParam, reportDatePeriod.getDateFrom(), reportDatePeriod.getDateTo(),
					Arrays.toString(objectIds), convertedFileType, isZip, keyValueStringer.get());

			rep.nmkGetReport(destReportType, inputStream, outputStream, idParam, reportDatePeriod.getDateFrom(),
					reportDatePeriod.getDateTo(), objectIds, convertedFileType, isZip, paramSpecialMap);

		} catch (IOException e) {
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
		ReportType result = ReportTypeKeynameConverter.CONVERTER_MAP.get(reportTypeKey);
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

	/**
	 *
	 * @param reportParamset
	 * @param reportDate
	 * @return
	 */
	private LocalDatePeriod getReportPeriod(ReportParamset reportParamset, LocalDateTime reportDate) {

		// Get Start and End dates
		LocalDateTime dtStart = null;
		LocalDateTime dtEnd = null;
		//--------------------------------------------------------------------
		if (reportParamset.getReportPeriodKey() == ReportPeriodKey.INTERVAL) {
			if (reportParamset.getParamsetStartDate() == null || reportParamset.getParamsetEndDate() == null) {
				throw new IllegalArgumentException(String.format(
						"ReportParamset (id=%d) is invalid. "
								+ "ParamsetStartDate and ParamsetEndDate is not set correctly. " + "ReportPeriodKey=%s",
						reportParamset.getId(), ReportPeriodKey.INTERVAL));
			}

			dtStart = reportDate.toLocalDate().atStartOfDay();
			dtEnd = reportDate.toLocalDate().plusDays(1).atStartOfDay().minusNanos(1);

		}
		//--------------------------------------------------------------------
		else if ((reportParamset.getReportPeriodKey() == ReportPeriodKey.LAST_MONTH
				&& reportParamset.getReportPeriodKey().isSettlementDay() && reportParamset.getSettlementDay() != null)
				|| (reportParamset.getReportPeriodKey() == ReportPeriodKey.SETTLEMENT_MONTH &&
				//reportParamset.getSettlementDay() != null
				//&&
						reportParamset.getReportPeriodKey().isSettlementDay()
						&& reportParamset.getSettlementMonth() != null && reportParamset.getSettlementYear() != null)) {

			LocalDateTime modReportDate = reportDate;
			if (reportParamset.getReportPeriodKey() == ReportPeriodKey.SETTLEMENT_MONTH) {
				if (reportParamset.getSettlementMonth() != null) {
					modReportDate = modReportDate.withMonth(reportParamset.getSettlementMonth());
				}
				if (reportParamset.getSettlementYear() != null) {
					modReportDate = modReportDate.withYear(reportParamset.getSettlementYear());
				}
			}

			// If Settlement day & LAST MONTH

			final int lastDayOfCurrMonth = modReportDate.toLocalDate().withDayOfMonth(1).plusMonths(1).minusDays(1).getMonthValue();
//                withMillisOfDay(0).withDayOfMonth(1).plusMonths(1).minusDays(1)
//					.getDayOfMonth();

			int settlementDay = reportParamset.getSettlementDay() != null ? reportParamset.getSettlementDay()
					: lastDayOfCurrMonth;

			int currentDayOfMonth = modReportDate.getDayOfMonth(); // withMillisOfDay(0).getDayOfMonth();

			if (currentDayOfMonth >= settlementDay && settlementDay <= lastDayOfCurrMonth) {
				dtEnd = modReportDate.toLocalDate().withDayOfMonth(settlementDay).minusDays(1).atStartOfDay();
//				dtEnd = JodaTimeUtils.endOfDay(modReportDate.withDayOfMonth(settlementDay).minusDays(1));
				dtStart = modReportDate.toLocalDate().withDayOfMonth(settlementDay).minusMonths(1).atStartOfDay();
//                    JodaTimeUtils.startOfDay(modReportDate.withDayOfMonth(settlementDay).minusMonths(1));
//				dtStart = JodaTimeUtils.startOfDay(modReportDate.withDayOfMonth(settlementDay).minusMonths(1));
			} else {

				try {
					final int lastDayOfPrev1Month = modReportDate.toLocalDate().withDayOfMonth(1).minusDays(1)
                        .getDayOfMonth();
//					final int lastDayOfPrev1Month = modReportDate.withMillisOfDay(0).withDayOfMonth(1).minusDays(1)
//							.getDayOfMonth();
					final int lastDayOfPrev2Month = modReportDate.toLocalDate().withDayOfMonth(1).minusMonths(1)
                        .minusDays(1).getDayOfMonth();
//					final int lastDayOfPrev2Month = modReportDate.withMillisOfDay(0).withDayOfMonth(1).minusMonths(1)
//							.minusDays(1).getDayOfMonth();
					if (settlementDay <= lastDayOfPrev1Month && settlementDay <= lastDayOfPrev2Month) {
						dtEnd = modReportDate.withDayOfMonth(settlementDay).minusDays(1).toLocalDate()
                            .plusDays(1).atStartOfDay().minusNanos(1);
//						dtEnd = JodaTimeUtils.endOfDay(modReportDate.withDayOfMonth(settlementDay).minusDays(1));
                        dtStart = modReportDate.minusMonths(1).withDayOfMonth(settlementDay).toLocalDate().atStartOfDay();
//						dtStart = JodaTimeUtils.startOfDay(modReportDate.minusMonths(1).withDayOfMonth(settlementDay));
					}

				} catch (Exception e) {
					logger.error("Can't calculate LAST_MONTH period. ReportDate = {}, settlementDate: {}",
							modReportDate, settlementDay);
				}
			}

			// If we havn't process dates
			if (dtStart == null || dtEnd == null) {
				LocalDateTime processedReportDate = reportDate;

				dtStart = ReportParamsetDateUtil.getStartDateTime(processedReportDate,
						reportParamset.getReportPeriodKey());
				dtEnd = ReportParamsetDateUtil.getEndDateTime(processedReportDate, reportParamset.getReportPeriodKey());
			}

		}
		//--------------------------------------------------------------------
		else {
			LocalDateTime modReportDate = reportDate;

			if (reportParamset.getReportPeriodKey() == ReportPeriodKey.SETTLEMENT_MONTH) {
				if (reportParamset.getSettlementMonth() != null) {
					modReportDate = modReportDate.withMonth(reportParamset.getSettlementMonth());
//					modReportDate = modReportDate.withMonthOfYear(reportParamset.getSettlementMonth());
				}
				if (reportParamset.getSettlementYear() != null) {
					modReportDate = modReportDate.withYear(reportParamset.getSettlementYear());
				}
			}

			dtStart = ReportParamsetDateUtil.getStartDateTime(modReportDate, reportParamset.getReportPeriodKey());
			dtEnd = ReportParamsetDateUtil.getEndDateTime(modReportDate, reportParamset.getReportPeriodKey());

		}

		return LocalDatePeriod.builder().dateFrom(dtStart).dateTo(dtEnd).build();
	}

}
