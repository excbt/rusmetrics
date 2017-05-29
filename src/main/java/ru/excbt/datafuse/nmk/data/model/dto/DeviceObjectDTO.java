package ru.excbt.datafuse.nmk.data.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by kovtonyk on 28.03.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class DeviceObjectDTO {

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeviceLoginInfoDTO  {
        private String deviceLogin;
        private String devicePassword;
    }



    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private Long deviceModelId;

    @Getter
    @Setter
    private String number;

    @Getter
    @Setter
    private String exCode;

    @Getter
    @Setter
    private String exLabel;

    @Getter
    @Setter
    private String exSystemKeyname;

    @Getter
    @Setter
    private int version;

    @Getter
    @Setter
    private Boolean isDeviceObjectMetadata;

    @Getter
    @Setter
    private Boolean isManual;

    @Getter
    @Setter
    private BigDecimal verificationInterval;

    @Getter
    @Setter
    private Date verificationDate;

    @Getter
    @Setter
    private Integer metaVersion = 1;

    @Getter
    @Setter
    private Boolean isHexPassword;

    @Getter
    @Setter
    private Boolean isTimeSyncEnabled;

    @Getter
    @Setter
    private Boolean isImpulse;

    @Getter
    @Setter
    private BigDecimal impulseK;

    @Getter
    @Setter
    private String impulseMu;

    @Getter
    @Setter
    private String impulseCounterAddr;

    @Getter
    @Setter
    private String impulseCounterSlotAddr;

    @Getter
    @Setter
    private String impulseCounterType;

    @Getter
    @Setter
    private DeviceLoginInfoDTO deviceLoginInfo;

    @Getter
    @Setter
    private ActiveDataSourceInfoDTO editDataSourceInfo;

    @Getter
    @Setter
    private String instType;

    @JsonIgnore
    public boolean isNew() {
        return id == null;
    }

    public void createDeviceLoginIngo() {
        this.deviceLoginInfo = new DeviceLoginInfoDTO();
    }


}
