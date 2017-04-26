package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kovtonyk on 10.04.2017.
 */
@Getter
@Setter
public class EnergyPassportShortDTO {

    private Long id;

    private Long subscriberId;

    private Long passportTemplateId;

    private String passportName;

    private LocalDate passportDate;

    private String description;

    private Long organizationId;

    private int version;

    public Date getPassportDate2() {
        return passportDate != null ? LocalDateUtils.asDate(passportDate) : null;
    }

    @Override
    public String toString() {
        return "EnergyPassportDTO{" +
            "id=" + id +
            ", subscriberId=" + subscriberId +
            ", passportTemplateId=" + passportTemplateId +
            ", passportName='" + passportName + '\'' +
            ", passportDate=" + passportDate +
            ", description='" + description + '\'' +
            ", organizationId=" + organizationId +
            ", version=" + version +
            '}';
    }
}
