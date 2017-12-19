package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;

import java.util.Date;

@Getter
@Setter
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

    private EditDataSourceDTO editDataSourceInfo;

    private String instType;

    private String deviceObjectName;

    private DeviceObjectTimeOffsetDTO timeOffset;

    private Long ContObjectId;

    private String ContObjectFullName;

    private String ContObjectName;

    private Long heatRadiatorTypeId;

    private Double heatRadiatorPower;

    private Long subscrDataSourceId;

    private String exSystemKeyname;

    private ActiveDataSourceInfoDTO activeDataSource;

    public DeviceObjectFullVM shareDeviceLoginInfo(DeviceObject deviceObject) {
        this.deviceLoginInfo = new DeviceObjectDTO.DeviceLoginInfoDTO(deviceObject);
        return this;
    }

    public void saveDeviceObjectCredentials(DeviceObject deviceObject) {
        if (deviceLoginInfo != null
            && deviceLoginInfo.getDeviceLogin() != null && deviceLoginInfo.getDevicePassword() != null
            ) {
            deviceObject.setDeviceLogin(deviceLoginInfo.getDeviceLogin() != null ? deviceLoginInfo.getDeviceLogin() : "");
            deviceObject.setDevicePassword(deviceLoginInfo.getDevicePassword() != null ? deviceLoginInfo.getDevicePassword() : "");
        }
    }


}
