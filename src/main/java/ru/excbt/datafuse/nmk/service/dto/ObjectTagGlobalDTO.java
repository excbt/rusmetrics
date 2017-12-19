package ru.excbt.datafuse.nmk.service.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"objectTagKeyname", "tagName"})
public class ObjectTagGlobalDTO {

    private String objectTagKeyname;

    private String tagName;

    private String tagColor;

    private String tagDescription;

    private String tagComment;

    private Boolean isEnabled;

    private String flexData;

    private int version;

}
