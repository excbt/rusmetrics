package ru.excbt.datafuse.nmk.data.model.support;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "dataDate", "detail_type", "work_time", "failTime", "h_delta", "h_in", "h_out", "m_delta", "m_in",
		"m_out", "p_delta", "p_in", "p_out", "t_cold", "t_in", "t_out", "t_outdoor", "v_delta", "v_in", "v_out" })
public abstract class ContServiceDataHWaterImport_CsvFormat {

	@JsonIgnore
	abstract Long getId();

	@JsonProperty("date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	abstract Date getDataDate();

	@JsonIgnore
	abstract String getCrc32Valid();

	@JsonIgnore
	abstract String getDataMstatus();

	@JsonIgnore
	abstract String getDataChanged();

	@JsonProperty("work_time")
	abstract BigDecimal getWorkTime();

	@JsonProperty("detail_type")
	abstract String getTimeDetailType();

}
