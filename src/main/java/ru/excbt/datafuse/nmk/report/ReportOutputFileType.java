package ru.excbt.datafuse.nmk.report;

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
}
