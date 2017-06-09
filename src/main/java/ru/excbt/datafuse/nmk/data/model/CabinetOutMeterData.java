package ru.excbt.datafuse.nmk.data.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Created by kovtonyk on 07.06.2017.
 */
@Entity
@Table(schema = "gate", name = "cabinet_out_meter_data")
@Getter
@Setter
public class CabinetOutMeterData {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "meter_period_id")
    private Long meterPeriodId;

    @Column(name = "device_object_id")
    private Long deviceObjectId;

    @Column(name = "period_year")
    private Integer periodYear;

    @Column(name = "period_mon")
    private Integer periodMon;

    @Column(name = "meter_value_type")
    private String meterValueType;

    @Column(name = "meter_value", columnDefinition = "numeric(12,2)")
    private Double meterValue;

    @Column(name = "cont_service_type")
    private String contServiceType;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Version
    private int version;

    @Column(name = "processed")
    private Boolean processed;

    @Column(name = "processed_datetime")
    private ZonedDateTime processedDateTime;
}
