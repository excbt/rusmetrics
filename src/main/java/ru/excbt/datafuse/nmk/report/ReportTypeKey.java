package ru.excbt.datafuse.nmk.report;

import java.util.Optional;
import java.util.stream.Stream;

public enum ReportTypeKey {
	COMMERCE_REPORT("commerce"), // CONS_REPORT,
	EVENT_REPORT("event"), //
	CONS_T1_REPORT("cons_t1"), //
	CONS_T2_REPORT("cons_t2"), //
	METROLOGICAL_REPORT("metrological"), //
	CONSUMPTION_REPORT("consumption"), //
	CONSUMPTION_HISTORY_REPORT("consumption_history"), //
	CONSUMPTION_ETALON_REPORT("consumption_etalon"), //
	LOG_JOURNAL_REPORT("log_journal");

	private final String urlName;
	private final String defaultFileName;

	private ReportTypeKey(String urlName) {
		this.urlName = urlName;
		this.defaultFileName = urlName;
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
}
