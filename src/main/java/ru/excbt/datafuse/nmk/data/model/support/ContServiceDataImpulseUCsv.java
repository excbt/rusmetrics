package ru.excbt.datafuse.nmk.data.model.support;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Created by kovtonyk on 31.05.2017.
 */
@Getter
@Setter
@JsonPropertyOrder({ "comment", "login", "serial", "dataDate", "dataValue"})
public class ContServiceDataImpulseUCsv {

    private String comment;

    @NotNull
    private String login;

    @NotNull
    private String serial;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataDate;

    private Double dataValue;

}
