package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;

public class ContServiceDataHWaterSummary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7928358602878254237L;

	private ContServiceDataHWaterTotals totals;

	private ContServiceDataHWaterTotals diffs;

	private ContServiceDataHWater firstData;

	private ContServiceDataHWater lastData;

	private ContServiceDataHWater average;

	public ContServiceDataHWater getFirstData() {
		return firstData;
	}

	public void setFirstData(ContServiceDataHWater firstData) {
		this.firstData = firstData;
	}

	public ContServiceDataHWater getLastData() {
		return lastData;
	}

	public void setLastData(ContServiceDataHWater lastData) {
		this.lastData = lastData;
	}

	public ContServiceDataHWaterTotals getTotals() {
		return totals;
	}

	public void setTotals(ContServiceDataHWaterTotals totals) {
		this.totals = totals;
	}

	public ContServiceDataHWaterTotals getDiffs() {
		return diffs;
	}

	public void setDiffs(ContServiceDataHWaterTotals diffs) {
		this.diffs = diffs;
	}

	public ContServiceDataHWater getAverage() {
		return average;
	}

	public void setAverage(ContServiceDataHWater average) {
		this.average = average;
	}

}
