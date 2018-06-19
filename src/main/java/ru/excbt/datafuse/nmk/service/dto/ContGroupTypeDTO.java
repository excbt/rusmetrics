package ru.excbt.datafuse.nmk.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContGroupTypeDTO {

    private Long id;

    private String contGroupTypeCaption;

    private String contGroupTypeName;

    private String contGroupTypeComment;

    private String contGroupTypeDescription;

    private int version;

}
