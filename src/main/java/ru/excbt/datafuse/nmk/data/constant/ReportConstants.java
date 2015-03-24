package ru.excbt.datafuse.nmk.data.constant;

public class ReportConstants {

	public final static String COMMERCIAL_REPORT_TEMPLATE_PATH = "COMMERCIAL_REPORT_TEMPLATE_PATH";
	public final static String EXTERNAL_JASPER_SERVER_ENABLE = "EXTERNAL_JASPER_SERVER_ENABLE";
	public final static String EXTERNAL_JASPER_SERVER_URL = "EXTERNAL_JASPER_SERVER_URL";

	public enum ReportType {
		HTML, PDF;

		public String toLowerName() {
			return this.name().toLowerCase();
		}
	};

	private ReportConstants() {

	}

}
