package ru.excbt.datafuse.nmk.data.model.support;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.util.Date;

@JsonPropertyOrder({ "dataDate", "timeDetailType", "dataValue"})
public abstract class ContServiceDataImpulse_CsvFormat {

    @JsonIgnore
    abstract Long getId();

    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    abstract Date getDataDate();

    @JsonIgnore
    abstract Long getContZpointId();

    @JsonIgnore
    abstract Long getDeviceObjectId();

    @JsonProperty("detail_type")
    abstract String getTimeDetailType();

    @JsonProperty("data_value")
    abstract BigDecimal getDataValue();

    @JsonIgnore
    abstract int getVersion();

    @JsonIgnore
    abstract int getDeleted();

	@JsonIgnore
	abstract String getDataDateString();

}
