package ru.excbt.datafuse.nmk.data.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.excbt.datafuse.nmk.data.model.ContManagement;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.MeterPeriodSetting;

import javax.persistence.*;
import java.util.*;

/**
 * Created by kovtonyk on 14.06.2017.
 */
@Getter
@Setter
public class ContObjectDTO {

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
