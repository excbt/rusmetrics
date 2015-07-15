package ru.excbt.datafuse.nmk.data.model.support;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ContServiceDataHWater_CsvFormat {

	@JsonProperty("date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	abstract Date getDataDate();

	@JsonIgnore
	abstract int getVersion();

	@JsonProperty("work_time")
	abstract BigDecimal getWorkTime();

	@JsonProperty("detail_type")
	abstract String getTimeDetailType();
}
