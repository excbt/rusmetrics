package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
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
public class Organization extends AbstractAuditableModel implements KeynameObject, DevModeObject, DeletableObject {

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
	private String organizationDecription;

	public String getExCode() {
		return exCode;
	}

	public void setExCode(String exCode) {
		this.exCode = exCode;
	}

	public String getExSystem() {
		return exSystem;
	}

	public void setExSystem(String exSystem) {
		this.exSystem = exSystem;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationFullName() {
		return organizationFullName;
	}

	public void setOrganizationFullName(String organizationFullName) {
		this.organizationFullName = organizationFullName;
	}

	public String getOrganizationFullAddress() {
		return organizationFullAddress;
	}

	public void setOrganizationFullAddress(String organizationFullAddress) {
		this.organizationFullAddress = organizationFullAddress;
	}

	public Boolean getFlagRso() {
		return flagRso;
	}

	public void setFlagRso(Boolean flagRso) {
		this.flagRso = flagRso;
	}

	public Boolean getFlagRma() {
		return flagRma;
	}

	public void setFlagRma(Boolean flagRma) {
		this.flagRma = flagRma;
	}

	@Override
	public String getKeyname() {
		return keyname;
	}

	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

	@Override
	public Boolean getIsDevMode() {
		return isDevMode;
	}

	public void setIsDevMode(Boolean isDevMode) {
		this.isDevMode = isDevMode;
	}

	public Boolean getFlagCm() {
		return flagCm;
	}

	public void setFlagCm(Boolean flagCm) {
		this.flagCm = flagCm;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public String getOrganizationDecription() {
		return organizationDecription;
	}

	public void setOrganizationDecription(String organizationDecription) {
		this.organizationDecription = organizationDecription;
	}
}
