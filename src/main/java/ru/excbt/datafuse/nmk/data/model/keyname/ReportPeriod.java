package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

@Entity
@Table(name = "report_period")
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

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@Column(name = "report_period_order")
	private Integer reportPeriodOrder;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSign() {
		return sign;
	}

	public void setSign(Integer sign) {
		this.sign = sign;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Boolean getIsSettlementDay() {
		return isSettlementDay;
	}

	public void setIsSettlementDay(Boolean isSettlementDay) {
		this.isSettlementDay = isSettlementDay;
	}

	@Override
	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public Integer getReportPeriodOrder() {
		return reportPeriodOrder;
	}

}
