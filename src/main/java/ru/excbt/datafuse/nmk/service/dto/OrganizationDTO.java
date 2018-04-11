package ru.excbt.datafuse.nmk.service.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.Organization;

/**
 * Created by kovtonyk on 23.06.2017.
 */
@Getter
@Setter
public class OrganizationDTO {

//    @Getter
//    public static class OrganizationInfo {
//
//        private final Long id;
//        private final String organizationName;
//
//        public OrganizationInfo(Organization organization) {
//            this.id = organization.getId();
//            this.organizationName = organization.getOrganizationName();
//        }
//
//    }

    private Long id;

    private String organizationName;

    private String organizationFullName;

    private String organizationFullAddress;

    private String exCode;

    private int version;

//    private int deleted;

    private String exSystem;

    private Boolean flagRso;

    private Boolean flagManagement;

    private Boolean flagRma;

    private String keyname;

    private Boolean isDevMode;

    private Boolean flagCm;

    private String organizationDescription;

    private Boolean isCommon;

    private Boolean flagServ;

    private String inn;

    private String kpp;

    private String okpo;

    private String ogrn;

    private String legalAddress;

    private String factAddress;

    private String postAddress;

    private String reqAccount;

    private String reqBankName;

    private String reqCorrAccount;

    private String reqBik;

    private String contactPhone;

    private String contactPhone2;

    private String contactPhone3;

    private String contactEmail;

    private String siteUrl;

    private String directorFio;

    private String chiefAccountantFio;

    private Long organizationTypeId;

    private String organizationTypeName;
}
