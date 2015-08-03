package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;

@Entity
@Table(name = "v_cont_object_hwater_delta")
public class ContObjectHWaterDelta extends AbstractPersistableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4406985098209145741L;

	@Column(name = "cont_object_id")
	private Long contObjectId;
	
	@Column(name = "cont_service_type")
	private String contServiceType;
	
	@Column(name = "cont_zpoint_id")
	private Long contZPointId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_date")
	private Date dataDate;

	@Column(name = "time_detail_type")
	private String timeDetailType;
	
	@Column(name = "t_in")
	private BigDecimal t_in;
	
	@Column(name = "t_out")
	private BigDecimal t_out;
	
	@Column(name = "m_delta")
	private BigDecimal m_delta;
	
	@Column(name = "v_delta")
	private BigDecimal v_delta;
	
	@Column(name = "h_delta")
	private BigDecimal h_delta;
	
	@Column(name = "work_time")
	private BigDecimal workTime;
	
	@Column(name = "fail_time")
	private BigDecimal failTime;

	public Long getContObjectId() {
		return contObjectId;
	}

	public String getContServiceType() {
		return contServiceType;
	}

	public Long getContZPointId() {
		return contZPointId;
	}

	public Date getDataDate() {
		return dataDate;
	}

	public String getTimeDetailType() {
		return timeDetailType;
	}

	public BigDecimal getT_in() {
		return t_in;
	}

	public BigDecimal getT_out() {
		return t_out;
	}

	public BigDecimal getM_delta() {
		return m_delta;
	}

	public BigDecimal getV_delta() {
		return v_delta;
	}

	public BigDecimal getH_delta() {
		return h_delta;
	}

	public BigDecimal getWorkTime() {
		return workTime;
	}

	public BigDecimal getFailTime() {
		return failTime;
	}
	
	
}
