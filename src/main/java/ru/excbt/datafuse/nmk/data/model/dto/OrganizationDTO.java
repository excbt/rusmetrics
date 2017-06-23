package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.Organization;

/**
 * Created by kovtonyk on 23.06.2017.
 */
@Getter
@Setter
public class OrganizationDTO {

    @Getter
    public static class OrganizationInfo {

        private final Long id;
        private final String organizationName;

        public OrganizationInfo(Organization organization) {
            this.id = organization.getId();
            this.organizationName = organization.getOrganizationName();
        }

    }

    private Long id;

    private String organizationName;

    private String organizationFullName;

    private String organizationFullAddress;

    private int version;

    private Boolean flagRso;

    private Boolean flagCm;

    private Boolean flagRma;

    private String keyname;

    private Boolean isDevMode;

    private int deleted;

    private String organizationDescription;

    private Boolean flagServ;

}
