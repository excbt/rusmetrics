package ru.excbt.datafuse.nmk.data.model;

/**
 * Created by kovtonyk on 06.06.2017.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_service_data_impulse_import")
@NamedStoredProcedureQueries({ @NamedStoredProcedureQuery(name = "importImpulseData",
    procedureName = "portal.process_service_data_impulse_import", parameters = {
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "par_session_uuid", type = String.class) }) })

@Getter
@Setter
public class ContServiceDataImpulseImport extends AbstractPersistableEntity<Long> {


    @Column(name = "data_date")
    private LocalDateTime dataDate;

    @Column(name = "cont_zpoint_id")
    private Long contZpointId;

    @Column(name = "device_object_id")
    private Long deviceObjectId;

    @Column(name = "time_detail_type")
    private String timeDetailType;

    @Column(name = "data_value", columnDefinition = "numeric")
    private Double dataValue;

    @Column(name = "data_raw", columnDefinition = "numeric")
    private Double dataRaw;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

    @Column(name = "created_by", updatable = false)
    @JsonIgnore
    private Long createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_by")
    private Long lastModifiedBy;

    //@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @JsonIgnore
    @Column(name = "trx_id")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID trxId;


}
