package ru.excbt.datafuse.nmk.service.vm;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ContObjectVM {

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
}
