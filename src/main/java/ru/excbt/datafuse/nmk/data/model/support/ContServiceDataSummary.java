package ru.excbt.datafuse.nmk.data.model.support;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
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

	private T firstDataAbs;

	private T lastDataAbs;

	private T diffsAbs;


}
