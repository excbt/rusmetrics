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

import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
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

}
