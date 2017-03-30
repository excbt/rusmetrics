package ru.excbt.datafuse.nmk.data.model.dmo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.excbt.datafuse.nmk.data.model.dto.DataSourceInfoDTO;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by kovtonyk on 28.03.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class DeviceObjectDMO {

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
    private Boolean isTimeSyncEnabled;


    @Getter
    @Setter
    private Boolean isHexPassword;

    @Getter
    @Setter
    private int version;


    @Getter
    @Setter
    private DeviceLoginInfoDTO deviceLoginInfo;

    @Getter
    @Setter
    private DataSourceInfoDMO editDataSourceInfo;

    @JsonIgnore
    public boolean isNew() {
        return id == null;
    }

    public void createDeviceLoginIngo() {
        this.deviceLoginInfo = new DeviceLoginInfoDTO();
    }


}
