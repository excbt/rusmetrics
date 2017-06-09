package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.domain.PersistableBuilder;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;
import ru.excbt.datafuse.nmk.data.model.markers.DevModeObject;
import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

/**
 * Организация
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 26.02.2015
 *
 */
@Entity
@Table(name = "organization")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Organization extends AbstractAuditableModel implements KeynameObject, DevModeObject, DeletableObject,
    PersistableBuilder<Organization, Long>{

    @Getter
	public static class OrganizationInfo {

		private final Long id;
		private final String organizationName;

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
	private Boolean isDevMode;

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

}
