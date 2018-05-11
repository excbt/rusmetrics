package ru.excbt.datafuse.nmk.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import ru.excbt.datafuse.nmk.config.Constants;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.domain.PersistableBuilder;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;
import ru.excbt.datafuse.nmk.data.model.markers.DevModeObject;
import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;
import ru.excbt.datafuse.nmk.domain.OrganizationType;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Организация
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 26.02.2015
 *
 */
@Entity
@Table(schema = Constants.DB_SCHEME_PORTAL, name = "organization")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Organization extends AbstractAuditableModel implements KeynameObject, DevModeObject, DeletableObject,
    PersistableBuilder<Organization, Long>{

    @Getter
    @Setter
	public static class OrganizationInfo {

		private Long id;
		private String organizationName;

        public OrganizationInfo() {
        }

        public OrganizationInfo(Organization organization) {
			this.id = organization.getId();
			this.organizationName = organization.getOrganizationName();
		}

	}

	/**
	 *
	 */
	private static final long serialVersionUID = -2192600082628553203L;

	@Column(name = "organization_name")
	private String organizationName;

	@Column(name = "organization_full_name")
	private String organizationFullName;

	@Column(name = "organization_full_address")
	private String organizationFullAddress;

	@JsonIgnore
	@Column(name = "ex_code")
	private String exCode;

	@JsonIgnore
	@Column(name = "ex_system")
	private String exSystem;

	@Version
	private int version;

	@Column(name = "flag_rso")
	private Boolean flagRso;

	@Column(name = "flag_cm")
	private Boolean flagCm;

	@Column(name = "flag_rma")
	private Boolean flagRma;

	@Column(name = "keyname")
	private String keyname;

	@Column(name = "is_dev_mode", insertable = false, updatable = false)
	private Boolean isDevMode = false;

	@Column(name = "deleted")
	private int deleted;

	@Column(name = "organization_description")
	private String organizationDescription;

	@JsonIgnore
	@Column(name = "is_common", insertable = false, updatable = false)
	private Boolean isCommon;

	@JsonIgnore
	@Column(name = "rma_subscriber_id")
	private Long rmaSubscriberId;

	@Column(name = "flag_serv")
	private Boolean flagServ;

    @Column(name = "inn")
    @Size(min = 10, max = 20)
    private String inn;

    @Column(name = "kpp")
    @Size(min = 9, max = 9)
    private String kpp;

    @Column(name = "okpo")
    @Size(min = 8, max = 10)
    private String okpo;

    @Column(name = "ogrn")
    private String ogrn;

    @Column(name = "legal_address")
    private String legalAddress;

    @Column(name = "fact_address")
    private String factAddress;

    @Column(name = "post_address")
    private String postAddress;

    @Column(name = "req_account")
    @Size(min = 20, max = 20)
    private String reqAccount;

    @Column(name = "req_bank_name")
    private String reqBankName;

    @Column(name = "req_corr_account")
    @Size(min = 20, max = 20)
    private String reqCorrAccount;

    @Column(name = "req_bik")
    @Size(min = 9, max = 9)
    private String reqBik;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "contact_phone2")
    private String contactPhone2;

    @Column(name = "contact_phone3")
    private String contactPhone3;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "site_url")
    private String siteUrl;

    @Column(name = "director_fio")
    private String directorFio;

    @Column(name = "chief_accountant_fio")
    private String chiefAccountantFio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_type_id")
    @BatchSize(size = 10)
    private OrganizationType organizationType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id")
    @BatchSize(size = 10)
    private Subscriber subscriber;

    @Column(name = "subscriber_id", insertable = false, updatable = false)
    private Long subscriberId;
}
