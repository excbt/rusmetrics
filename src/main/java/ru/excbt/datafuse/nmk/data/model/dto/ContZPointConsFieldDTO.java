package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ContZPointConsFieldDTO {

    @NotNull
    private String fieldName;
    @NotNull
    private Boolean isEnabled;

}
