package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Тарифный план
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 06.04.2015
 *
 */
@Entity
@Table(name = "tariff_plan")
public class TariffPlan extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8488959042650892902L;

	@ManyToOne
	@JoinColumn(name = "rso_organization_id")
	private Organization rso;

	@Column(name = "rso_organization_id", updatable = false, insertable = false)
	private Long rsoOrganizationId;

	//	@JsonIgnore
	//	@ManyToOne
	//	@JoinColumn(name = "subscriber_id")
	//	private Subscriber subscriber;

	@Column(name = "subscriber_id")
	private Long subscriberId;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "tariff_plan_cont_object", joinColumns = @JoinColumn(name = "tariff_plan_id"),
			inverseJoinColumns = @JoinColumn(name = "cont_object_id"))
	@JsonIgnore
	private List<ContObject> contObjects = new ArrayList<>();

	@Column(name = "tariff_plan_name")
	private String tariffPlanName;

	@Column(name = "tariff_plan_description")
	private String tariffPlanDescription;

	@Column(name = "tariff_plan_comment")
	private String tariffPlanComment;

	@ManyToOne
	@JoinColumn(name = "tariff_type_id")
	private TariffType tariffType;

	@Column(name = "tariff_type_id", insertable = false, updatable = false)
	private Long tariffTypeId;

	//	@JsonIgnore
	//	@ManyToOne(fetch = FetchType.LAZY)
	//	@JoinColumn(name = "tariff_option", insertable = false, updatable = false)
	//	private TariffOption tariffOption;

	@Column(name = "tariff_option")
	private String tariffOptionKeyname;

	@Column(name = "tariff_plan_value")
	private BigDecimal tariffPlanValue;

	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@Column(name = "is_default", insertable = false, updatable = false)
	private boolean _default;

	@Column(name = "is_default")
	private Boolean isDefault;

	@Version
	private int version;

	public Organization getRso() {
		return rso;
	}

	public void setRso(Organization rso) {
		this.rso = rso;
	}

	//	public Subscriber getSubscriber() {
	//		return subscriber;
	//	}
	//
	//	public void setSubscriber(Subscriber subscriber) {
	//		this.subscriber = subscriber;
	//	}

	// public ContObject getContObject() {
	// return contObject;
	// }

	// public void setContObject(ContObject contObject) {
	// this.contObject = contObject;
	// }

	public String getTariffPlanName() {
		return tariffPlanName;
	}

	public void setTariffPlanName(String tariffPlanName) {
		this.tariffPlanName = tariffPlanName;
	}

	public String getTariffPlanDescription() {
		return tariffPlanDescription;
	}

	public void setTariffPlanDescription(String tariffPlanDescription) {
		this.tariffPlanDescription = tariffPlanDescription;
	}

	public String getTariffPlanComment() {
		return tariffPlanComment;
	}

	public void setTariffPlanComment(String tariffPlanComment) {
		this.tariffPlanComment = tariffPlanComment;
	}

	//	public TariffOption getTariffOption() {
	//		return tariffOption;
	//	}
	//
	//	public void setTariffOption(TariffOption tariffOption) {
	//		this.tariffOption = tariffOption;
	//	}

	public BigDecimal getTariffPlanValue() {
		return tariffPlanValue;
	}

	public void setTariffPlanValue(BigDecimal tariffPlanValue) {
		this.tariffPlanValue = tariffPlanValue;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public TariffType getTariffType() {
		return tariffType;
	}

	public void setTariffType(TariffType tariffType) {
		this.tariffType = tariffType;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public List<ContObject> getContObjects() {
		return contObjects;
	}

	public void setContObjects(List<ContObject> contObjects) {
		this.contObjects = contObjects;
	}

	private boolean is_default() {
		return _default;
	}

	private void set_default(boolean _default) {
		this._default = _default;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getTariffOptionKeyname() {
		return tariffOptionKeyname;
	}

	public void setTariffOptionKeyname(String tariffOptionKeyname) {
		this.tariffOptionKeyname = tariffOptionKeyname;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public Long getRsoOrganizationId() {
		return rsoOrganizationId;
	}

	public void setRsoOrganizationId(Long rsoOrganizationId) {
		this.rsoOrganizationId = rsoOrganizationId;
	}

	public Long getTariffTypeId() {
		return tariffTypeId;
	}

	public void setTariffTypeId(Long tariffTypeId) {
		this.tariffTypeId = tariffTypeId;
	}

}
