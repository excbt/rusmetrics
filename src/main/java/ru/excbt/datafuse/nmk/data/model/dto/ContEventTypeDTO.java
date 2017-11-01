package ru.excbt.datafuse.nmk.data.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.mapstruct.factory.Mappers;
import ru.excbt.datafuse.nmk.service.mapper.ContEventTypeMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContEventTypeDTO {

    public static final ContEventTypeMapper MAPPER = Mappers.getMapper( ContEventTypeMapper.class );

    private Long id;

    private String keyname;

    private String name;

    private Long parentId;

    @JsonIgnore
    private Integer contEventLevel;

    private String contEventCategoryKeyname;

    @JsonIgnore
    private Long reverseId;

    private Boolean isBaseEvent;

    private Boolean isCriticalEvent;

    private Boolean isScalarEvent;

    @JsonIgnore
    private Boolean isDevMode;

    @JsonIgnore
    private Boolean isSmsNotification;

    @JsonIgnore
    private String smsMessageTemplate;

    private String levelColor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyname() {
        return keyname;
    }

    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getContEventLevel() {
        return contEventLevel;
    }

    public void setContEventLevel(Integer contEventLevel) {
        this.contEventLevel = contEventLevel;
    }

    public String getContEventCategoryKeyname() {
        return contEventCategoryKeyname;
    }

    public void setContEventCategoryKeyname(String contEventCategoryKeyname) {
        this.contEventCategoryKeyname = contEventCategoryKeyname;
    }

    public Long getReverseId() {
        return reverseId;
    }

    public void setReverseId(Long reverseId) {
        this.reverseId = reverseId;
    }

    public Boolean getBaseEvent() {
        return isBaseEvent;
    }

    public void setBaseEvent(Boolean baseEvent) {
        isBaseEvent = baseEvent;
    }

    public Boolean getCriticalEvent() {
        return isCriticalEvent;
    }

    public void setCriticalEvent(Boolean criticalEvent) {
        isCriticalEvent = criticalEvent;
    }

    public Boolean getScalarEvent() {
        return isScalarEvent;
    }

    public void setScalarEvent(Boolean scalarEvent) {
        isScalarEvent = scalarEvent;
    }

    public Boolean getDevMode() {
        return isDevMode;
    }

    public void setDevMode(Boolean devMode) {
        isDevMode = devMode;
    }

    public Boolean getSmsNotification() {
        return isSmsNotification;
    }

    public void setSmsNotification(Boolean smsNotification) {
        isSmsNotification = smsNotification;
    }

    public String getSmsMessageTemplate() {
        return smsMessageTemplate;
    }

    public void setSmsMessageTemplate(String smsMessageTemplate) {
        this.smsMessageTemplate = smsMessageTemplate;
    }

    public String getLevelColor() {
        return levelColor;
    }

    public void setLevelColor(String levelColor) {
        this.levelColor = levelColor;
    }
}
