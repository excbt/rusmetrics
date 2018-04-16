package ru.excbt.datafuse.nmk.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.util.FlexDataToString;

@Getter
@Setter
public class OrganizationTypeDTO {

    private Long id;

    private String typeKeyname;

    private String typeName;

    private Boolean enabled;

    private Integer sortOrder;

    @JsonRawValue
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "flexUI")
    @JsonDeserialize(using = FlexDataToString.class)
    private String flexUI;
}
