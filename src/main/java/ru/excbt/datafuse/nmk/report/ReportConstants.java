package ru.excbt.datafuse.nmk.report;

/**
 * Константы для работы с отчетами
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.03.2015
 *
 */
public class ReportConstants {

	public final static String COMMERCIAL_REPORT_TEMPLATE_PATH = "COMMERCIAL_REPORT_TEMPLATE_PATH";
	public final static String EXTERNAL_JASPER_SERVER_ENABLE = "EXTERNAL_JASPER_SERVER_ENABLE";
	public final static String EXTERNAL_JASPER_SERVER_URL = "EXTERNAL_JASPER_SERVER_URL";

	public static final boolean IS_COMPILED = true;
	public static final boolean IS_NOT_COMPILED = false;

	public static final boolean IS_ACTIVE = true;
	public static final boolean IS_NOT_ACTIVE = false;

	public static final String EXT_JASPER = ".jasper";
	public static final String EXT_JRXML = ".jrxml";
	public static final String EXT_PRPT = ".prpt";
    public static final String EXT_XLSX = ".xlsx";

	public static class Files {

		public static final String COMM_FILE_COMPILED = "jasper_reports/nmk_com_report.jasper";
		public static final String COMM_FILE_JRXML = "jasper_reports/nmk_com_report.jrxml";
		public static final String EVENT_FILE_COMPILED = "jasper_reports/nmk_event_report.jasper";
		public static final String CONS_T1_FILE_COMPILED = "jasper_reports/nmk_consolidated_report_1.jasper";
		public static final String CONS_T2_FILE_COMPILED = "jasper_reports/nmk_consolidated_report_2.jasper";
		public static final String METROLOGICAL_FILE_COMPILED = "jasper_reports/nmk_metrological_rep.jasper";
		public static final String CONSUMPTION_FILE_COMPILED = "jasper_reports/nmk_consumption_report.jasper"; // deprecated
		public static final String CONSUMPTION_HISTORY_FILE_COMPILED = "jasper_reports/nmk_consumption_history_report.jasper"; // deprecated
		public static final String CONSUMPTION_HISTORY_ETALON_FILE_COMPILED = "jasper_reports/nmk_consumption_history_report.jasper"; // deprecated
		public static final String LOG_JOURNAL_FILE_COMPILED = "jasper_reports/nmk_log_journal.jasper";
		public static final String PARTNER_SERVICE_FILE_COMPILED = "jasper_reports/nmk_partner_service_rep.jasper";
		public static final String ABONENT_SERVICE_FILE_COMPILED = "jasper_reports/nmk_abonent_service_rep.jasper";
		public static final String CONSUMPTION_HISTORY_V2_FILE_COMPILED = "jasper_reports/nmk_consumption_history_report_v2.jasper";
		public static final String CONSUMPTION_HISTORY_ETALON_V2_FILE_COMPILED = "jasper_reports/nmk_consumption_history_report_v2.jasper";
		public static final String CONSUMPTION_V1_1_FILE_COMPILED = "jasper_reports/nmk_consumption_report_v1.1.jasper";
		public static final String CONSUMPTION_ETALON_FILE_COMPILED = "jasper_reports/nmk_consumption_etalon_report.jasper";
		public static final String RMA_ABONENT_SERVICE_FILE_COMPILED = "jasper_reports/nmk_abonent_service_rep.jasper";
		public static final String ELECTRIC_READINGS_FILE_COMPILED = "jasper_reports/nmk_electric_readings_report.jasper";
		public static final String HW_QUALITY_FILE_COMPILED = "jasper_reports/nmk_hw_quality_report.prpt";
		public static final String COMM_M_V_FILE_COMPILED = "jasper_reports/nmk_com_report_m_v.jasper";
		public static final String ELECTRIC_CONSUMPTION_FILE_COMPILED = "jasper_reports/nmk_electric_consumption_report.prpt";
		public static final String HW_QUALITY_SHEET_FILE_COMPILED = "jasper_reports/nmk_hw_quality_sheet_report.prpt";
		public static final String HW_QUALITY_SHEET_HOUR_FILE_COMPILED = "jasper_reports/nmk_hw_quality_sheet_hour_report.prpt";
		public static final String HW_QUALITY_ACT_1_FILE_COMPILED = "jasper_reports/nmk_hw_quality_act_1_report.prpt";
		public static final String HW_QUALITY_ACT_2_FILE_COMPILED = "jasper_reports/nmk_hw_quality_act_2_report.prpt";
		public static final String HW_DATA_FILE_COMPILED = "jasper_reports/nmk_hw_data_report.prpt";
		public static final String EL_QUALITY_FILE_COMPILED = "jasper_reports/nmk_el_quality_report.prpt";
		public static final String ELECTRIC_CONSUMPTION_ABONENT_FILE_COMPILED = "jasper_reports/nmk_electric_consumption_abonent_report.jasper";
		public static final String HW_LAST_EVENT_FILE_COMPILED = "jasper_reports/nmk_hw_last_event_report.prpt";
		public static final String CW_CONSUMPTION_FILE_COMPILED = "jasper_reports/nmk_cw_consumption_report.prpt";
		public static final String HW_CALC_CONSUMPTION_BY_AVG_FILE_COMPILED = "jasper_reports/nmk_hw_calc_consumption_by_avg_report.jasper";
		public static final String HEAT_AVG_FORECAST_FILE_COMPILED = "jasper_reports/nmk_heat_avg_forecast_report.prpt";
		public static final String COMM_ZPOINT_FILE_COMPILED = "jasper_reports/nmk_com_zpoint_report.jasper";
		public static final String HW_DATA_ZPOINT_FILE_COMPILED = "jasper_reports/nmk_hw_data_zpoint_report.prpt";
		public static final String CONSOLIDATED_ZPOINT_FILE_COMPILED = "jasper_reports/nmk_consolidated_zpoint_report.jasper";
		public static final String HEAT_QUALITY_CHART_FILE_COMPILED = "jasper_reports/nmk_heat_quality_chart_report.jasper";
		public static final String HW_BAD_PERIODS_FILE_COMPILED = "jasper_reports/nmk_hw_bad_periods_report.prpt";
		public static final String HW_QUALITY_1_FILE_COMPILED = "jasper_reports/nmk_hw_quality_1_report.prpt";
		public static final String HW_QUALITY_2_FILE_COMPILED = "jasper_reports/nmk_hw_quality_2_report.prpt";
		public static final String RMA_ZPOINT_STAT_FILE_COMPILED = "jasper_reports/nmk_rma_zpoint_stat_report.prpt";
        public static final String ELECTRIC_CONSUMPTION_EXPORT_FILE_COMPILED = "jasper_reports/nmk_electric_consumption_export.xlsx";
        public static final String NO_DATA_FILE_COMPILED = "jasper_reports/nmk_no_data_report.prpt";
        public static final String ELECTRIC_T5_FILE_COMPILED = "jasper_reports/nmk_electric_t5_report.prpt";

		private Files() {

		}

	}

	private ReportConstants() {

	}

	/**
	 *
	 * @param rtk
	 * @return
	 */
	public static String getReportTypeURL(ReportTypeKey rtk) {
		String result = null;
		switch (rtk) {
		case CONS_T1_REPORT: {
			result = "/cons_t1";
			break;
		}
		case CONS_T2_REPORT: {
			result = "/cons_t2";
			break;
		}
		case COMMERCE_REPORT: {
			result = "/commerce";
			break;
		}
		case EVENT_REPORT: {
			result = "/event";
			break;
		}
		case METROLOGICAL_REPORT: {
			result = "/metrological";
			break;
		}
		default:
			result = "";
			break;
		}
		return result;
	}

	/**
	 *
	 * @param rtk
	 * @return
	 */
	public static String getReportTypeURL(String rtk) {
		ReportTypeKey key = ReportTypeKey.valueOf(rtk);
		return getReportTypeURL(key);
	}

}
