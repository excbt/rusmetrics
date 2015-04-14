package ru.excbt.datafuse.nmk.data.constant;

public class ReportConstants {

	public final static String COMMERCIAL_REPORT_TEMPLATE_PATH = "COMMERCIAL_REPORT_TEMPLATE_PATH";
	public final static String EXTERNAL_JASPER_SERVER_ENABLE = "EXTERNAL_JASPER_SERVER_ENABLE";
	public final static String EXTERNAL_JASPER_SERVER_URL = "EXTERNAL_JASPER_SERVER_URL";

	public enum ReportOutputType {
		HTML, PDF;

		public String toLowerName() {
			return this.name().toLowerCase();
		}
	};

	public enum ReportTypeKeys {
		COMMERCE_REPORT, CONS_REPORT, EVENT_REPORT
	}

	public enum ReportPeriodKeys {
		CURRENT_MONTH, INTERVAL, LAST_MONTH, TODAY, YESTERDAY
	}

	public enum ReportActionKeys {
		EMAIL_LIST_DELIVERY
	}

	public enum ReportSheduleKeys {
		DAILY, MONTHLY, SINGLE, WEEKLY
	}

	private ReportConstants() {

	}

}
