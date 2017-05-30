package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.markers.DevModeObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kovtonyk on 26.05.2017.
 */
@Getter
@Setter
public class DeviceModelDTO implements DevModeObject {

    private Long id;

    private String modelName;

    private String keyname;

    private String caption;

    private String exCode;

    private String exLabel;

    private String exSystem;

    private String exSystemKeyname;

    private int version;

    private Boolean isDevMode;

    private int deleted;

    private Integer metaVersion;

    private Boolean isImpulse;

    private BigDecimal defaultImpulseK;

    private String defaultImpulseMu;

    private String deviceType;

    private Map<Long,Double> heatRadiatorKcs = new HashMap<>();

}
