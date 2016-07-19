package ru.excbt.datafuse.nmk.report;

import java.util.Optional;
import java.util.stream.Stream;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

/**
 * Типы отчетов
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.07.2015
 *
 */
public enum ReportTypeKey implements KeynameObject {
	COMMERCE_REPORT(2, "commerce"), // COMMERCE_M_V,
	COMMERCE_REPORT_M_V(20, "commerce_m_v"), // COMMERCE_M_V,
	EVENT_REPORT(12, "event"), //
	CONS_T1_REPORT(3, "cons_t1"), //
	CONS_T2_REPORT(4, "cons_t2"), //
	METROLOGICAL_REPORT(14, "metrological"), //
	CONSUMPTION_ETALON_REPORT(5, "consumption_etalon"), //
	CONSUMPTION_REPORT(10, "consumption"), // // deprecated
	CONSUMPTION_REPORT_V1_1(11, "consumption_v1_1"), //
	CONSUMPTION_HISTORY_REPORT(6, "consumption_history"), // // deprecated
	CONSUMPTION_HISTORY_REPORT_V2(8, "consumption_history_v2"), //
	CONSUMPTION_HISTORY_ETALON_REPORT_V2(9, "consumption_history_etalon_v2"), //
	CONSUMPTION_HISTORY_ETALON_REPORT(7, "consumption_history_etalon"), // deprecated
	LOG_JOURNAL_REPORT(13, "log_journal"), //
	PARTNER_SERVICE_REPORT(15, "partner_service"), //
	ABONENT_SERVICE_REPORT(1, "abonent_service"), //
	RMA_ABONENT_SERVICE_REPORT(16, "rma_abonent_service"), //
	ELECTRIC_READINGS_REPORT(18, "electric_readings", ReportSystem.PENTAHO), //
	HW_QUALITY_REPORT(19, "hw_quality", ReportSystem.PENTAHO), //
	ELECTRIC_CONSUMPTION_REPORT(21, "electric_consumption", ReportSystem.PENTAHO), //
	HW_QUALITY_SHEET_REPORT(22, "hw_quality_sheet", ReportSystem.PENTAHO), //
	HW_QUALITY_SHEET_HOUR_REPORT(23, "hw_quality_sheet_hour", ReportSystem.PENTAHO), //
	HW_QUALITY_ACT_1_REPORT(24, "hw_quality_act_1", ReportSystem.PENTAHO), //
	HW_QUALITY_ACT_2_REPORT(25, "hw_quality_act_2", ReportSystem.PENTAHO), //
	HW_DATA_REPORT(26, "hw_data", ReportSystem.PENTAHO), //
	EL_QUALITY_REPORT(27, "el_quality", ReportSystem.PENTAHO), ELECTRIC_CONSUMPTION_ABONENT_REPORT(28, "electric_consumption_abonent"), //
	HW_LAST_EVENT_REPORT(29, "hw_last_event", ReportSystem.PENTAHO), //
	CW_CONSUMPTION_REPORT(30, "cw_consumption", ReportSystem.PENTAHO), //
	HW_CALC_CONSUMPTION_BY_AVG_REPORT(31, "hw_calc_consumption_by_avg"), //
	HEAT_AVG_FORECAST_REPORT(32, "heat_avg_forecast", ReportSystem.PENTAHO), //
	COMMERCE_ZPOINT_REPORT(33, "commerce_zpoint"), //
	HW_DATA_ZPOINT_REPORT(34, "hw_data_zpoint", ReportSystem.PENTAHO); //

	private final String urlName;
	private final String defaultFileName;
	private final ReportSystem reportSystem;
	private final int reportId;

	private ReportTypeKey(int reportId, String urlName) {
		this.urlName = urlName;
		this.defaultFileName = urlName;
		this.reportSystem = ReportSystem.JASPER;
		this.reportId = reportId;
	}

	private ReportTypeKey(int reportId, String urlName, ReportSystem reportSystem) {
		this.urlName = urlName;
		this.defaultFileName = urlName;
		this.reportSystem = reportSystem;
		this.reportId = reportId;
	}

	public String getUrlName() {
		return urlName;
	}

	/**
	 * 
	 * @param urlName
	 * @return
	 */
	public static ReportTypeKey findByUrlName(String urlName) {
		Optional<ReportTypeKey> opt = Stream.of(ReportTypeKey.values()).filter((i) -> i.urlName.equals(urlName))
				.findFirst();

		return opt.isPresent() ? opt.get() : null;
	}

	public String getDefaultFileName() {
		return defaultFileName;
	}

	@Override
	public String getKeyname() {
		return this.name();
	}

	public ReportSystem getReportSystem() {
		return reportSystem;
	}

	public int getReportId() {
		return reportId;
	}

}
