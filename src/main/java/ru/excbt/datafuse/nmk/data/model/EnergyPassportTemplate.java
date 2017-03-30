package ru.excbt.datafuse.nmk.data.model;

/**
 * Created by kovtonyk on 29.03.2017.
 */

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "energy_passport_template")
@Getter
@Setter
public class EnergyPassportTemplate extends AbstractAuditableModel {

    @Column(name = "keyname")
    private String keyname;

    @Column(name = "description")
    private String description;

    @Column(name = "document_name")
    private String documentName;

    @Column(name = "document_version")
    private Integer documentVersion;

    @Column(name = "document_date")
    private LocalDate documentDate;

}
