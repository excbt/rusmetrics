package ru.excbt.datafuse.nmk.data.service;

import java.io.Serializable;

public class ContServiceDataSummary<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1072712784973103737L;

	private T totals;

	private T diffs;

	private T firstData;

	private T lastData;

	private T average;

	public T getTotals() {
		return totals;
	}

	public void setTotals(T totals) {
		this.totals = totals;
	}

	public T getDiffs() {
		return diffs;
	}

	public void setDiffs(T diffs) {
		this.diffs = diffs;
	}

	public T getFirstData() {
		return firstData;
	}

	public void setFirstData(T firstData) {
		this.firstData = firstData;
	}

	public T getLastData() {
		return lastData;
	}

	public void setLastData(T lastData) {
		this.lastData = lastData;
	}

	public T getAverage() {
		return average;
	}

	public void setAverage(T average) {
		this.average = average;
	}
}
