package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSourceTypeDTO {

    private String keyname;

    private String caption;

    private Boolean isRaw;

    private Boolean isDB;

    private String typeName;

    private String typeDescription;

    private String typeComment;

    private Integer typeOrder;

    private Boolean isDbTableEnable;

    private Boolean isDbTablePair;

}
