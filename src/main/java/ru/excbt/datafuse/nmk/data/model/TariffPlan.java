 package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.constant.TariffPlanConstant.TariffOptionKey;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.TariffOption;

@Entity
@Table(name = "tariff_plan")
//@EntityListeners({AuditingEntityListener.class})
public class TariffPlan extends AbstractAuditableModel  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8488959042650892902L;

	@ManyToOne
	@JoinColumn(name = "rso_organization_id")
	private Organization rso;

	@ManyToOne
	@JoinColumn(name = "subscriber_id")
	private Subscriber subscriber;

	@ManyToOne
	@JoinColumn(name = "cont_object_id")
	private ContObject contObject;
	
	@Column (name = "tariff_plan_name")
	private String tariffPlanName;
	
	@Column (name = "tariff_plan_description")	
	private String tariffPlanDescription;
	
	@Column (name = "tariff_plan_comment")
	private String tariffPlanComment;

	@ManyToOne
	@JoinColumn(name = "tariff_type_id")
	private TariffType tariffType;
	
	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn(name = "tariff_option", insertable = false, updatable = false)
	private TariffOption tariffOption;
	
	@Enumerated(EnumType.STRING)	
	@Column(name="tariff_option")
	private TariffOptionKey tariffOptionKey;
	
	@Column(name = "tariff_plan_value")
	private BigDecimal tariffPlanValue;
	
	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@Version
	private int version;
	
	public Organization getRso() {
		return rso;
	}

	public void setRso(Organization rso) {
		this.rso = rso;
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public ContObject getContObject() {
		return contObject;
	}

	public void setContObject(ContObject contObject) {
		this.contObject = contObject;
	}

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

	public TariffOption getTariffOption() {
		return tariffOption;
	}

	public void setTariffOption(TariffOption tariffOption) {
		this.tariffOption = tariffOption;
	}

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

	public TariffOptionKey getTariffOptionKey() {
		return tariffOptionKey;
	}

	public void setTariffOptionKey(TariffOptionKey tariffOptionKey) {
		this.tariffOptionKey = tariffOptionKey;
	}
	
}
