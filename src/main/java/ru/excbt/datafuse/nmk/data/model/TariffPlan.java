 package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.TariffOption;

@Entity
@Table(name = "tariff_plan")
@EntityListeners({ AuditingEntityListener.class })
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
	@JoinColumn(name = "tariff_option")
	private TariffOption tariffOption;
	
	@Column(name = "tariff_plan_value")
	private Double tariffPlanValue;
	
	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

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

	public Double getTariffPlanValue() {
		return tariffPlanValue;
	}

	public void setTariffPlanValue(Double tariffPlanValue) {
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
	
}
