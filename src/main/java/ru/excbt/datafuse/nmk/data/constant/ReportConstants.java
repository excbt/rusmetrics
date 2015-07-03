package ru.excbt.datafuse.nmk.data.constant;

public class ReportConstants {

	public final static String COMMERCIAL_REPORT_TEMPLATE_PATH = "COMMERCIAL_REPORT_TEMPLATE_PATH";
	public final static String EXTERNAL_JASPER_SERVER_ENABLE = "EXTERNAL_JASPER_SERVER_ENABLE";
	public final static String EXTERNAL_JASPER_SERVER_URL = "EXTERNAL_JASPER_SERVER_URL";

	public static final boolean IS_COMPILED = true;
	public static final boolean IS_NOT_COMPILED = false;

	public static final boolean IS_ACTIVE = true;
	public static final boolean IS_NOT_ACTIVE = false;

	public static class Paths {

		public static final String COMM_FILE_COMPILED = "jasper_reports/nmk_com_report.jasper";
		public static final String COMM_FILE_JRXML = "jasper_reports/nmk_com_report.jrxml";
		public static final String EVENT_FILE_COMPILED = "jasper_reports/nmk_event_report.jasper";
		public static final String CONS_T1_FILE_COMPILED = "jasper_reports/nmk_consolidated_report_1.jasper";
		public static final String CONS_T2_FILE_COMPILED = "jasper_reports/nmk_consolidated_report_2.jasper";
		public static final String METROLOGICAL_FILE_COMPILED = "jasper_reports/nmk_metrological_rep.jasper";

		private Paths() {

		}

	}

	public enum ReportOutputFileType {
		HTML("text/html", ".html"), PDF("application/pdf", ".pdf"), XLS(
				"application/vnd.ms-excel", ".xlsx"), XLSX(
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				".xlsx");

		private final String mimeType;
		private final String ext;

		private ReportOutputFileType(String mimeType, String ext) {
			this.mimeType = mimeType;
			this.ext = ext;
		}

		public String toLowerName() {
			return this.name().toLowerCase();
		}

		public String getMimeType() {
			return mimeType;
		}

		public String getExt() {
			return ext;
		}
	};

	public enum ReportTypeKey {
		COMMERCE_REPORT, // CONS_REPORT,
		EVENT_REPORT, CONS_T1_REPORT, CONS_T2_REPORT, METROLOGICAL_REPORT
	}

	public enum ReportPeriodKey {
		CURRENT_MONTH, INTERVAL, LAST_MONTH, TODAY, YESTERDAY, DAY
	}

	public enum ReportActionKey {
		EMAIL_LIST_DELIVERY, EMAIL_RAW_DELIVERY
	}

	public enum ReportSheduleTypeKey {
		DAILY, MONTHLY, SINGLE, WEEKLY
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

}
