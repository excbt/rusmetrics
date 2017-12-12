package ru.excbt.datafuse.nmk.data.model.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectShortInfo;
import ru.excbt.datafuse.nmk.data.model.support.ObjectAccess;
import ru.excbt.datafuse.nmk.data.model.support.ObjectAccessInitializer;
import ru.excbt.datafuse.nmk.data.util.FlexDataToString;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by kovtonyk on 14.06.2017.
 */
@Getter
@Setter
public class ContObjectDTO implements ObjectAccessInitializer {

    private Long id;

    private String name;

    private String fullName;

    private String fullAddress;

    private String number;

    private String owner;

    private String ownerContacts;

    private String currentSettingMode;

    private Date settingModeMDate;

    private String description;

    private String comment;

    private Double cwTemp;

    private Double heatArea;

    private int version;

    private String timezoneDefKeyname;

    private String exSystemKeyname;

    private Boolean isManual;

    private Boolean _haveSubscr;

    private String _daDataSraw;

    private Boolean isAddressAuto;

    private Boolean isValidFiasUUID;

    private Boolean isValidGeoPos;

    private String buildingType;

    private String buildingTypeCategory;

    private Integer numOfStories;

    private ContManagementDTO _activeContManagement;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private ObjectAccess access;

    @JsonRawValue
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "flexData")
    @JsonDeserialize(using = FlexDataToString.class)
    private String flexData;

    @Override
    public void objectAccess(ObjectAccess.AccessType accessType, LocalDate date) {
        ObjectAccess access = new ObjectAccess();
        access.setAccessTTL(date);
        access.setAccessType(accessType);
        this.access = access;
    }


    public ContObjectShortInfo newShortInfo() {
        return new ShortInfo(this);
    }


    public static class ShortInfo implements ContObjectShortInfo {

        private final ContObjectDTO contObjectDTO;

        public ShortInfo(ContObjectDTO contObjectDTO) {
            this.contObjectDTO = contObjectDTO;
        }


        @Override
        public Long getContObjectId() {
            return contObjectDTO.getId();
        }

        @Override
        public String getName() {
            return contObjectDTO.name;
        }

        @Override
        public String getFullName() {
            return contObjectDTO.fullName;
        }
    }

}
