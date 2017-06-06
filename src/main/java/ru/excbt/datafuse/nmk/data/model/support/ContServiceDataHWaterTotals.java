package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class ContServiceDataHWaterTotals implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 616264830719948979L;

	private Long contZPointId;

	private Date beginDate;

	private Date endDate;

	private String timeDetailType;

	private Double m_in;

	private Double m_out;

	private Double m_delta;

	private Double v_in;

	private Double v_out;

	private Double v_delta;

	private Double h_in;

	private Double h_out;

	private Double h_delta;



}
