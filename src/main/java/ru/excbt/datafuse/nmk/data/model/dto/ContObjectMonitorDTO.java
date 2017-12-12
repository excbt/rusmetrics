package ru.excbt.datafuse.nmk.data.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.support.ObjectAccess;
import ru.excbt.datafuse.nmk.data.model.support.ObjectAccessInitializer;

import java.time.LocalDate;
import java.util.Date;

/**
 * Created by kovtonyk on 14.06.2017.
 */
@Getter
@Setter
public class ContObjectMonitorDTO implements ObjectAccessInitializer {


    @Getter
    @Setter
    public static class ContObjectStats {
        private int contZpointCount;

        private String contEventLevelColor;

        private long eventsCount;

        private long eventsTypesCount;
    }

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

    private long newEventsCount;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private ObjectAccess access;

    private ContObjectStats contObjectStats = new ContObjectStats();

    @Override
    public void objectAccess(ObjectAccess.AccessType accessType, LocalDate date) {
        ObjectAccess access = new ObjectAccess();
        access.setAccessTTL(date);
        access.setAccessType(accessType);
        this.access = access;
    }


}
