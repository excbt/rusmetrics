package ru.excbt.datafuse.nmk.data.model;

/**
 * Created by kovtonyk on 26.05.2017.
 */

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.PersistableBuilder;

import javax.persistence.*;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "heat_radiator_type")
@Getter
@Setter
public class HeatRadiatorType implements PersistableBuilder<HeatRadiatorType, Long> {

    @Id
    @SequenceGenerator(name = "abstractEntity", sequenceName = "seq_global_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "abstractEntity")
    @Column(name = "id")
    private Long id;

    @Column(name = "type_name")
    private String typeName;

    @Column(name = "type_description")
    private String typeDescription;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "order_idx")
    private Integer orderIdx;

}
