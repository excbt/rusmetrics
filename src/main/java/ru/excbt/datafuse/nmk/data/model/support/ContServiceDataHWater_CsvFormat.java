package ru.excbt.datafuse.nmk.data.model.support;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ContServiceDataHWater_CsvFormat {

	@JsonIgnore
	abstract Long getId();

	@JsonProperty("date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	abstract Date getDataDate();

	@JsonIgnore
	abstract int getVersion();

	@JsonIgnore
	abstract String getDataDateString();

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
