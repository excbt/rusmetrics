package ru.excbt.datafuse.nmk.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_zpoint_consumption")
@Getter
@Setter
public class ContZPointConsumption {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "contZPointConsumptionSeq", sequenceName = "portal.cont_zpoint_consumption_id_seq", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contZPointConsumptionSeq")
    private Long id;

    @Column(name = "cont_zpoint_id")
    private Long contZPointId;

    @Column(name = "cont_service_type")
    private String contServiceType;

    @Column(name = "time_detail_type")
    private String timeDetailType;

    @Column(name = "date_from")
    private LocalDateTime dateFrom;

    @Column(name = "date_to")
    private LocalDateTime dateTo;

    @Column(name = "last_calc_time")
    private LocalDateTime lastCalcTime = LocalDateTime.now();

    @Column(name = "last_start_time")
    private LocalDateTime lastStartTime = LocalDateTime.now();

    @Column(name = "status")
    private String status;

    @Column(name = "measure_unit")
    private String measureUnit;

    @Column(name = "cons_func")
    private String consFunc;

    @Type( type = "double-array" )
    @Column(name = "cons_data", columnDefinition = "float[]")
    private double[] consData;

    @Size(max = 32)
    @Column(name = "cons_data_md5")
    private String consDataMD5;

}
