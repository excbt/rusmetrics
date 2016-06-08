package ru.excbt.datafuse.nmk.report;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Типы периодов отчетов
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.07.2015
 *
 */
public enum ReportPeriodKey {
	CURRENT_MONTH, INTERVAL, LAST_MONTH(true), TODAY, YESTERDAY, DAY, SETTLEMENT_MONTH(true);

	private final boolean isSettlementDay;

	/**
	 * 
	 * @param isSettlementDay
	 */
	private ReportPeriodKey(boolean isSettlementDay) {
		this.isSettlementDay = isSettlementDay;
	}

	/**
	 * 
	 */
	private ReportPeriodKey() {
		this.isSettlementDay = false;
	}

	/**
	 * 
	 * @return
	 */
	@JsonIgnore
	public boolean isSettlementDay() {
		return isSettlementDay;
	}

}
