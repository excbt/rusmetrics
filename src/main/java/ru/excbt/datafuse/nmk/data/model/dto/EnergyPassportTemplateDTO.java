package ru.excbt.datafuse.nmk.data.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kovtonyk on 30.03.2017.
 */
@Getter
@Setter
@ToString
public class EnergyPassportTemplateDTO {

    private Long id;

    private String keyname;

    private String description;

    private String documentName;

    private Integer documentVersion;

    private LocalDate documentDate;

    private List<EnergyPassportSectionTemplateDTO> sectionTemplates = new ArrayList<>();

    public void addSection(EnergyPassportSectionTemplateDTO section) {

    }

}
