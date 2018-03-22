package ru.excbt.datafuse.nmk.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationTypeDTO {

    private Long id;

    private String typeKeyname;

    private String typeName;

    private Boolean enabled;

}
