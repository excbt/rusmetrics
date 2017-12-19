package ru.excbt.datafuse.nmk.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.util.FlexDataToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonPropertyOrder({"objectTagKeyname", "tagName"})
public class ObjectTagInfoDTO {

    @NotNull
    private String objectTagKeyname;

    @NotNull
    private String tagName;

    private String tagColor;

    private String tagDescription;

    private String tagComment;

    private Boolean isEnabled = true;

    @JsonRawValue
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "flexData")
    @JsonDeserialize(using = FlexDataToString.class)
    private String flexData;

    private int version;

}
