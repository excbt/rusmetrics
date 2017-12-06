package ru.excbt.datafuse.nmk.data.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.excbt.datafuse.nmk.data.domain.ModelIdable;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.support.MaxCheck;
import ru.excbt.datafuse.nmk.data.model.support.TimeDetailLastDate;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Created by kovtonyk on 07.07.2017.
 */
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

    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Set<String> tagNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContObjectId() {
        return contObjectId;
    }

    public void setContObjectId(Long contObjectId) {
        this.contObjectId = contObjectId;
    }

    public ContServiceTypeDTO getContServiceType() {
        return contServiceType;
    }

    public void setContServiceType(ContServiceTypeDTO contServiceType) {
        this.contServiceType = contServiceType;
    }


    public String getContServiceTypeKeyname() {
        return contServiceTypeKeyname;
    }

    public void setContServiceTypeKeyname(String contServiceTypeKeyname) {
        this.contServiceTypeKeyname = contServiceTypeKeyname;
    }

    public String getCustomServiceName() {
        return customServiceName;
    }

    public void setCustomServiceName(String customServiceName) {
        this.customServiceName = customServiceName;
    }

    public OrganizationDTO getRso() {
        return rso;
    }

    public void setRso(OrganizationDTO rso) {
        this.rso = rso;
    }

    public Long getRsoId() {
        return rsoId;
    }

    public void setRsoId(Long rsoId) {
        this.rsoId = rsoId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(String checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public Integer getCheckoutDay() {
        return checkoutDay;
    }

    public void setCheckoutDay(Integer checkoutDay) {
        this.checkoutDay = checkoutDay;
    }

    public Boolean getDoublePipe() {
        return doublePipe;
    }

    public void setDoublePipe(Boolean doublePipe) {
        this.doublePipe = doublePipe;
    }

    public Boolean getManualLoading() {
        return isManualLoading;
    }

    public void setManualLoading(Boolean manualLoading) {
        isManualLoading = manualLoading;
    }

    public String getExSystemKeyname() {
        return exSystemKeyname;
    }

    public void setExSystemKeyname(String exSystemKeyname) {
        this.exSystemKeyname = exSystemKeyname;
    }

    public String getExCode() {
        return exCode;
    }

    public void setExCode(String exCode) {
        this.exCode = exCode;
    }

    public Integer getTsNumber() {
        return tsNumber;
    }

    public void setTsNumber(Integer tsNumber) {
        this.tsNumber = tsNumber;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public String getContZPointComment() {
        return contZPointComment;
    }

    public void setContZPointComment(String contZPointComment) {
        this.contZPointComment = contZPointComment;
    }

    public Boolean getDroolsDisable() {
        return isDroolsDisable;
    }

    public void setDroolsDisable(Boolean droolsDisable) {
        isDroolsDisable = droolsDisable;
    }

    public Long getTemperatureChartId() {
        return temperatureChartId;
    }

    public void setTemperatureChartId(Long temperatureChartId) {
        this.temperatureChartId = temperatureChartId;
    }

    public Date getLastDataDate() {
        if (timeDetailLastDates.size() > 0) {
            final MaxCheck<Date> maxCheck = new MaxCheck<>();

            timeDetailLastDates.forEach(i -> maxCheck.check(i.getDataDate()));

            return maxCheck.getObject();
        }

        return null;
    }

    public DeviceObjectFullVM getDeviceObject() {
        return deviceObject;
    }

    public void setDeviceObject(DeviceObjectFullVM deviceObject) {
        this.deviceObject = deviceObject;
    }

    public Long getDeviceObjectId() {
        return deviceObjectId;
    }

    public void setDeviceObjectId(Long deviceObjectId) {
        this.deviceObjectId = deviceObjectId;
    }

    public List<TimeDetailLastDate> getTimeDetailLastDates() {
        return timeDetailLastDates;
    }

    public Set<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(Set<String> tagNames) {
        this.tagNames = tagNames;
    }
}
