package ru.excbt.datafuse.nmk.data.model.dto;

import java.util.Date;

public class DeviceObjectFullVM {

    private Long id;

    private DeviceModelDTO deviceModel;

    private Long deviceModelId;

    private String number;

    private int version;

    private Boolean isDeviceObjectMetadata;

    private Boolean isManual;

    private Double verificationInterval;

    private Date verificationDate;

    private Integer metaVersion = 1;

    private Boolean isHexPassword;

    private Boolean isTimeSyncEnabled;

    private Boolean isImpulse;

    private Double impulseK;

    private String impulseMu;

    private String impulseCounterAddr;

    private String impulseCounterSlotAddr;

    private String impulseCounterType;

    private DeviceObjectDTO.DeviceLoginInfoDTO deviceLoginInfo;

    private ActiveDataSourceInfoDTO editDataSourceInfo;

    private String instType;

    private String deviceObjectName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeviceModelDTO getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(DeviceModelDTO deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Boolean getDeviceObjectMetadata() {
        return isDeviceObjectMetadata;
    }

    public void setDeviceObjectMetadata(Boolean deviceObjectMetadata) {
        isDeviceObjectMetadata = deviceObjectMetadata;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public Double getVerificationInterval() {
        return verificationInterval;
    }

    public void setVerificationInterval(Double verificationInterval) {
        this.verificationInterval = verificationInterval;
    }

    public Date getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(Date verificationDate) {
        this.verificationDate = verificationDate;
    }

    public Integer getMetaVersion() {
        return metaVersion;
    }

    public void setMetaVersion(Integer metaVersion) {
        this.metaVersion = metaVersion;
    }

    public Boolean getHexPassword() {
        return isHexPassword;
    }

    public void setHexPassword(Boolean hexPassword) {
        isHexPassword = hexPassword;
    }

    public Boolean getTimeSyncEnabled() {
        return isTimeSyncEnabled;
    }

    public void setTimeSyncEnabled(Boolean timeSyncEnabled) {
        isTimeSyncEnabled = timeSyncEnabled;
    }

    public Boolean getImpulse() {
        return isImpulse;
    }

    public void setImpulse(Boolean impulse) {
        isImpulse = impulse;
    }

    public Double getImpulseK() {
        return impulseK;
    }

    public void setImpulseK(Double impulseK) {
        this.impulseK = impulseK;
    }

    public String getImpulseMu() {
        return impulseMu;
    }

    public void setImpulseMu(String impulseMu) {
        this.impulseMu = impulseMu;
    }

    public String getImpulseCounterAddr() {
        return impulseCounterAddr;
    }

    public void setImpulseCounterAddr(String impulseCounterAddr) {
        this.impulseCounterAddr = impulseCounterAddr;
    }

    public String getImpulseCounterSlotAddr() {
        return impulseCounterSlotAddr;
    }

    public void setImpulseCounterSlotAddr(String impulseCounterSlotAddr) {
        this.impulseCounterSlotAddr = impulseCounterSlotAddr;
    }

    public String getImpulseCounterType() {
        return impulseCounterType;
    }

    public void setImpulseCounterType(String impulseCounterType) {
        this.impulseCounterType = impulseCounterType;
    }

    public DeviceObjectDTO.DeviceLoginInfoDTO getDeviceLoginInfo() {
        return deviceLoginInfo;
    }

    public void setDeviceLoginInfo(DeviceObjectDTO.DeviceLoginInfoDTO deviceLoginInfo) {
        this.deviceLoginInfo = deviceLoginInfo;
    }

    public ActiveDataSourceInfoDTO getEditDataSourceInfo() {
        return editDataSourceInfo;
    }

    public void setEditDataSourceInfo(ActiveDataSourceInfoDTO editDataSourceInfo) {
        this.editDataSourceInfo = editDataSourceInfo;
    }

    public String getInstType() {
        return instType;
    }

    public void setInstType(String instType) {
        this.instType = instType;
    }

    public String getDeviceObjectName() {
        return deviceObjectName;
    }

    public void setDeviceObjectName(String deviceObjectName) {
        this.deviceObjectName = deviceObjectName;
    }

    public Long getDeviceModelId() {
        return deviceModelId;
    }

    public void setDeviceModelId(Long deviceModelId) {
        this.deviceModelId = deviceModelId;
    }
}
