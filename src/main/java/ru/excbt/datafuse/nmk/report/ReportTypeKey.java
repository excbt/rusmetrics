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
	COMMERCE_REPORT("commerce"), // CONS_REPORT,
	EVENT_REPORT("event"), //
	CONS_T1_REPORT("cons_t1"), //
	CONS_T2_REPORT("cons_t2"), // // deprecated
	METROLOGICAL_REPORT("metrological"), //
	CONSUMPTION_ETALON_REPORT("consumption_etalon"), //
	CONSUMPTION_REPORT("consumption"), // // deprecated
	CONSUMPTION_REPORT_V1_1("consumption_v1_1"), //
	CONSUMPTION_HISTORY_REPORT("consumption_history"), // // deprecated
	CONSUMPTION_HISTORY_REPORT_V2("consumption_history_v2"), //
	CONSUMPTION_HISTORY_ETALON_REPORT_V2("consumption_history_etalon_v2"), //
	CONSUMPTION_HISTORY_ETALON_REPORT("consumption_history_etalon"), // deprecated
	LOG_JOURNAL_REPORT("log_journal"), //
	PARTNER_SERVICE_REPORT("partner_service"), //
	ABONENT_SERVICE_REPORT("abonent_service"), //
	RMA_ABONENT_SERVICE_REPORT("rma_abonent_service"), //
	ELECTRIC_READINGS_REPORT("electric_readings", ReportSystem.PENTAHO), //
	HW_QUALITY_REPORT("hw_quality", ReportSystem.PENTAHO);

	private final String urlName;
	private final String defaultFileName;
	private final ReportSystem reportSystem;

	private ReportTypeKey(String urlName) {
		this.urlName = urlName;
		this.defaultFileName = urlName;
		this.reportSystem = ReportSystem.JASPER;
	}

	private ReportTypeKey(String urlName, ReportSystem reportSystem) {
		this.urlName = urlName;
		this.defaultFileName = urlName;
		this.reportSystem = reportSystem;
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
		Optional<ReportTypeKey> opt = Stream.of(ReportTypeKey.values())
				.filter((i) -> i.urlName.equals(urlName)).findFirst();

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
}
