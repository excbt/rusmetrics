package ru.excbt.datafuse.nmk.data.constant;

public class ReportConstants {

	public final static String COMMERCIAL_REPORT_TEMPLATE_PATH = "COMMERCIAL_REPORT_TEMPLATE_PATH";
	public final static String EXTERNAL_JASPER_SERVER_ENABLE = "EXTERNAL_JASPER_SERVER_ENABLE";
	public final static String EXTERNAL_JASPER_SERVER_URL = "EXTERNAL_JASPER_SERVER_URL";

	public enum ReportOutputFileType {
		HTML, PDF, ZIP, XLS;

		public String toLowerName() {
			return this.name().toLowerCase();
		}
	};

	public enum ReportTypeKey {
		COMMERCE_REPORT, //CONS_REPORT, 
		EVENT_REPORT, CONS_T1_REPORT, CONS_T2_REPORT
	}

	public enum ReportPeriodKey {
		CURRENT_MONTH, INTERVAL, LAST_MONTH, TODAY, YESTERDAY
	}

	public enum ReportActionKey {
		EMAIL_LIST_DELIVERY,
		EMAIL_RAW_DELIVERY
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
			result = "/cons";
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
		default:
			result = "";
			break;
		}
		return result;
	}

}
