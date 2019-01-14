package ru.excbt.datafuse.nmk.data.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.ModelIdable;
import ru.excbt.datafuse.nmk.data.util.FlexDataToString;
import ru.excbt.datafuse.nmk.data.model.support.MaxCheck;
import ru.excbt.datafuse.nmk.data.model.support.TimeDetailLastDate;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Created by kovtonyk on 07.07.2017.
 */
@Getter
@Setter
public class ContZPointFullVM implements ModelIdable {

    private Long id;

    @NotNull
    private Long contObjectId;

    @Deprecated
    private ContServiceTypeDTO contServiceType;

    @NotNull
    private String contServiceTypeKeyname;

    private String customServiceName;

    @Deprecated
    private OrganizationDTO rso;

    @NotNull
    private Long rsoId;

    @NotNull
    private Date startDate;

    private Date endDate;

    private int version;

    private String checkoutTime;

    private Integer checkoutDay;

    private Boolean doublePipe;

    private Boolean isManualLoading;

    private String exSystemKeyname;

    private String exCode;

    private Integer tsNumber;

    private Boolean isManual;

    private String contZPointComment;

    private Boolean isDroolsDisable;

    private Long temperatureChartId;

    private DeviceObjectFullVM deviceObject;

    private Long deviceObjectId;

    private List<TimeDetailLastDate> timeDetailLastDates = new ArrayList<>();

    @JsonInclude(Include.USE_DEFAULTS)
    private Set<String> tagNames;

    @JsonRawValue
    @JsonInclude(Include.NON_NULL)
    @JsonProperty(value = "flexData")
    @JsonDeserialize(using = FlexDataToString.class)
    private String flexData;

    private Set<ContZPointConsFieldDTO> consFields = new HashSet<>();

    public Date getLastDataDate() {
        if (timeDetailLastDates.size() > 0) {
            final MaxCheck<Date> maxCheck = new MaxCheck<>();

            timeDetailLastDates.forEach(i -> maxCheck.check(i.getDataDate()));

            return maxCheck.getObject();
        }

        return null;
    }

    public void setTimeDetailLastDates(List<TimeDetailLastDate> timeDetailLastDates) {
        Objects.requireNonNull(timeDetailLastDates);
        this.timeDetailLastDates = timeDetailLastDates;
    }

}
