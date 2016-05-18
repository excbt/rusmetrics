package ru.excbt.datafuse.nmk.report;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import ru.excbt.nmk.reports.NmkReport.ReportType;

/**
 * 
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.02.2016
 *
 */
public class ReportTypeKeynameConverter {

	public final static Map<ReportTypeKey, ReportType> CONVERTER_MAP;

	private final static Map<ReportTypeKey, ReportType> REPORTS_TYPE_KEYNAME_TEMP = new HashMap<>();;

	private final static Set<Integer> REPORT_TYPE_ID_CHECK = new HashSet<>();

	private ReportTypeKeynameConverter() {
	}

	/**
	 * 
	 */
	static {
		initReportKeynames();
		CONVERTER_MAP = Collections.unmodifiableMap(Maps.newHashMap(REPORTS_TYPE_KEYNAME_TEMP));
	}

	/**
	 * 
	 * @param reportTypeMap
	 * @param reportTypeKey
	 * @param nmkReportType
	 */
	private static void addPair(ReportTypeKey reportTypeKey, ReportType nmkReportType) {
		if (reportTypeKey.getReportId() != nmkReportType.getReportId()) {
			throw new IllegalArgumentException("Report Types ID's is not equals: ReportTypeKey=" + reportTypeKey
					+ ", Nmk ReportType=" + nmkReportType);
		}
		if (REPORT_TYPE_ID_CHECK.contains(reportTypeKey.getReportId())) {
			throw new IllegalArgumentException(
					"Report Types ID's is already registered. id=" + reportTypeKey.getReportId());
		}
		REPORT_TYPE_ID_CHECK.add(reportTypeKey.getReportId());
		REPORTS_TYPE_KEYNAME_TEMP.put(reportTypeKey, nmkReportType);
	}

	/**
	 * 
	 */
	private static void initReportKeynames() {
		Set<ReportTypeKey> setE = EnumSet.allOf(ReportTypeKey.class);
		for (ReportTypeKey reportType : setE) {
			ReportType nmkReportType = ReportType.searchFor(reportType.getReportId());
			if (nmkReportType == null) {
				throw new IllegalStateException("Report Type is not found: ReportTypeKey=" + reportType);
			}
			addPair(reportType, nmkReportType);
		}

	}

}
