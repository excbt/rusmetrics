/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.widget;

import java.math.BigDecimal;
import java.util.Date;

import ru.excbt.datafuse.nmk.data.model.markers.DataDateFormatter;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.12.2016
 * 
 */
public class HeatWidgetTemperatureDto implements DataDateFormatter {

	private Date dataDate;
	private String timeDetailType;
	private BigDecimal t_in;
	private BigDecimal t_out;
	private BigDecimal chartT_in;
	private BigDecimal chartT_out;
	private BigDecimal t_ambiance;

	@Override
	public Date getDataDate() {
		return dataDate;
	}

	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}

	@Override
	public String getTimeDetailType() {
		return timeDetailType;
	}

	public void setTimeDetailType(String timeDetailType) {
		this.timeDetailType = timeDetailType;
	}

	public BigDecimal getT_in() {
		return t_in;
	}

	public void setT_in(BigDecimal t_in) {
		this.t_in = t_in;
	}

	public BigDecimal getT_out() {
		return t_out;
	}

	public void setT_out(BigDecimal t_out) {
		this.t_out = t_out;
	}

	public BigDecimal getChartT_in() {
		return chartT_in;
	}

	public void setChartT_in(BigDecimal chartT_in) {
		this.chartT_in = chartT_in;
	}

	public BigDecimal getChartT_out() {
		return chartT_out;
	}

	public void setChartT_out(BigDecimal chartT_out) {
		this.chartT_out = chartT_out;
	}

	public BigDecimal getT_ambiance() {
		return t_ambiance;
	}

	public void setT_ambiance(BigDecimal t_ambiance) {
		this.t_ambiance = t_ambiance;
	}

	@Override
	public String toString() {
		return String.format(
				"HeatWidgetTemperature [dataDate=%s, t_in=%s, t_out=%s, chartT_in=%s, chartT_out=%s, t_ambiance=%s]",
				dataDate, t_in, t_out, chartT_in, chartT_out, t_ambiance);
	}

}
