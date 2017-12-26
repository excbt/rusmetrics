package ru.excbt.datafuse.nmk.data.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.keyname.DataSourceType;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscrDataSourceDTO {

    private Long id;

    private Long subscriberId;

    private String keyname;

    private String caption;

    private String dataSourceTypeKey;

    private DataSourceTypeDTO dataSourceType;

    private String dataSourceName;

    private String dataSourceDescription;

    private String dataSourceComment;

    private String dataSourceIp;

    private String dataSourcePort;

    private int version;

    private int deleted;

    private String dbName;

    private String dbUser;

    private String dbPassword;

    // Transient
    private Boolean _isAnotherSubscriber;

    private Integer rawTimeout;

    private Integer rawSleepTime;

    private Integer rawResendAttempts;

    private Integer rawReconnectAttempts;

    private Integer rawReconnectTimeout;

    private String rawConnectionType;

    private Long rawModemModelId;

    private String rawModemSerial;

    private String rawModemMacAddr;

    private String rawModemImei;

    private Boolean rawModemDialEnable;

    private String rawModemDialTel;

    private Boolean enabled = false;

}
