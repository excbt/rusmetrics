package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

@Entity
@Table(name = "report_period")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
public class ReportPeriod extends AbstractKeynameEntity implements DisabledObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 7709872986189209116L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "report_period_name")
	private String name;

	@Column(name = "report_period_sign")
	private Integer sign;

	@Column(name = "report_period_unit")
	private String unit;

	@Version
	private int version;

	@Column(name = "is_settlement_day")
	private Boolean isSettlementDay;

	@Column(name = "is_settlement_month")
	private Boolean isSettlementMonth;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@Column(name = "report_period_order")
	private Integer reportPeriodOrder;

}
