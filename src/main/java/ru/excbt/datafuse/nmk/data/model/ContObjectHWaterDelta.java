package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DataDateFormatter;

/**
 * Расширенные данные HWater
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 03.08.2015
 *
 */
@Entity
@Table(name = "v_cont_object_hwater_delta")
@Getter
public class ContObjectHWaterDelta extends AbstractPersistableEntity<Long> implements DataDateFormatter {

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

}
