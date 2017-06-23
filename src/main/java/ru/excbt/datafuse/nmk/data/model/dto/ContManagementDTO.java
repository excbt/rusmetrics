package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by kovtonyk on 23.06.2017.
 */
@Getter
@Setter
public class ContManagementDTO {

    private Long id;

    private String agreementNr;

    private Date agreementDate;

    private Date beginDate;

    private Date endDate;

    private OrganizationDTO organization;

    private Long organizationId;

    private String reportsPath;

    private int version;

}
