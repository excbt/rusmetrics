package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(name = "cont_management")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContManagement extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1593276105973101936L;

	@Column(name = "agreement_nr")
	private String agreementNr;

	@Column(name = "agreement_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date agreementDate;

	@Column(name = "cm_begin_date")
	@Temporal(TemporalType.DATE)
	private Date beginDate;

	@Column(name = "cm_end_date")
	@Temporal(TemporalType.DATE)
	private Date endDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cont_object_id")
	@JsonIgnore
	private ContObject contObject;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@Column(name = "organization_id", insertable = false, updatable = false)
	private Long organizationId;

	@Column(name = "reports_path")
	private String reportsPath;

	@Version
	private int version;

	public String getAgreementNr() {
		return agreementNr;
	}

	public void setAgreementNr(String agreementNr) {
		this.agreementNr = agreementNr;
	}

	public Date getAgreementDate() {
		return agreementDate;
	}

	public void setAgreementDate(Date agreementDate) {
		this.agreementDate = agreementDate;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public ContObject getContObject() {
		return contObject;
	}

	public void setContObject(ContObject contObject) {
		this.contObject = contObject;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public String getReportsPath() {
		return reportsPath;
	}

	public void setReportsPath(String reportsPath) {
		this.reportsPath = reportsPath;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

}
