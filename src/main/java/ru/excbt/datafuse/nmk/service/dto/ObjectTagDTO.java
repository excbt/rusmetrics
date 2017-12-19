package ru.excbt.datafuse.nmk.service.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"objectTagKeyname", "tagName"})
public class ObjectTagDTO {

    private String objectTagKeyname;

    private Long objectId;

    private String tagName;

}
