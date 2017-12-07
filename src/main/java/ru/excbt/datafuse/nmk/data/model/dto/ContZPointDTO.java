package ru.excbt.datafuse.nmk.data.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ru.excbt.datafuse.nmk.data.domain.ModelIdable;
import ru.excbt.datafuse.nmk.data.util.FlexDataToString;

import java.util.*;

/**
 * Created by kovtonyk on 07.07.2017.
 */
public class ContZPointDTO implements ModelIdable {

    private Long id;

    private Long contObjectId;

    private String contServiceTypeKeyname;

    private String customServiceName;

    private Long rsoId;

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

    private Long deviceObjectId;

    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Set<String> tagNames;

    @JsonRawValue
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "flexData")
    @JsonDeserialize(using = FlexDataToString.class)
    private String flexData;


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

    public Long getDeviceObjectId() {
        return deviceObjectId;
    }

    public void setDeviceObjectId(Long deviceObjectId) {
        this.deviceObjectId = deviceObjectId;
    }

    public Set<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(Set<String> tagNames) {
        this.tagNames = tagNames;
    }


    public String getFlexData() {
        return flexData;
    }

    public void setFlexData(String flexData) {
        this.flexData = flexData;
    }
}
