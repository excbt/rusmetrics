package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
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

}
