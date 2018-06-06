package ru.excbt.datafuse.nmk.service.vm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;

@Getter
@Setter
public class ContObjectShortInfoVM {

    private Long contObjectId;

    private String contObjectName;

    private String contObjectFullName;

    private String currentSettingMode;

    private String buildingType;

    private String buildingTypeCategory;

    private String contObjectFullAddress;

    @JsonIgnore
    public String getNameSortingKey() {
        String key = getUiCaption();
        return key != null ? key.toLowerCase() : key;
    }

    public String getUiCaption() {
        return ObjectUtils.firstNonNull(contObjectName, contObjectFullName, contObjectFullAddress);
    }

}
