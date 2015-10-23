package ru.excbt.datafuse.nmk.report;

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

	public static class Files {

		public static final String COMM_FILE_COMPILED = "jasper_reports/nmk_com_report.jasper";
		public static final String COMM_FILE_JRXML = "jasper_reports/nmk_com_report.jrxml";
		public static final String EVENT_FILE_COMPILED = "jasper_reports/nmk_event_report.jasper";
		public static final String CONS_T1_FILE_COMPILED = "jasper_reports/nmk_consolidated_report_1.jasper";
		public static final String CONS_T2_FILE_COMPILED = "jasper_reports/nmk_consolidated_report_2.jasper";
		public static final String METROLOGICAL_FILE_COMPILED = "jasper_reports/nmk_metrological_rep.jasper";
		public static final String CONSUMPTION_FILE_COMPILED = "jasper_reports/nmk_consumption_report.jasper";
		public static final String CONSUMPTION_HISTORY_FILE_COMPILED = "jasper_reports/nmk_consumption_history_report.jasper";
		public static final String CONSUMPTION_ETALON_FILE_COMPILED = "jasper_reports/nmk_consumption_history_report.jasper";
		public static final String LOG_JOURNAL_FILE_COMPILED = "jasper_reports/nmk_log_journal.jasper";
		public static final String PARTNER_SERVICE_FILE_COMPILED = "jasper_reports/nmk_partner_service_rep.jasper";
		public static final String ABONENT_SERVICE_FILE_COMPILED = "jasper_reports/nmk_abonent_service_rep.jasper";

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
