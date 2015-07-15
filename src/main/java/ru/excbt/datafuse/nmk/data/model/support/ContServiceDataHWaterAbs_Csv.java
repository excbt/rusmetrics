package ru.excbt.datafuse.nmk.data.model.support;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import org.apache.commons.beanutils.BeanUtils;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;

public class ContServiceDataHWaterAbs_Csv extends ContServiceDataHWater {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2942100515881972912L;

	private BigDecimal m_in_abs;

	private BigDecimal m_out_abs;

	private BigDecimal v_in_abs;

	private BigDecimal v_out_abs;

	private BigDecimal h_delta_abs;

	public ContServiceDataHWaterAbs_Csv() {

	}

	public static ContServiceDataHWaterAbs_Csv newInstance(
			ContServiceDataHWater parent) throws IllegalAccessException,
			InvocationTargetException {
		ContServiceDataHWaterAbs_Csv result = new ContServiceDataHWaterAbs_Csv();
		BeanUtils.copyProperties(result, parent);
		return result;
	}

	public BigDecimal getM_in_abs() {
		return m_in_abs;
	}

	public void setM_in_abs(BigDecimal m_in_abs) {
		this.m_in_abs = m_in_abs;
	}

	public BigDecimal getM_out_abs() {
		return m_out_abs;
	}

	public void setM_out_abs(BigDecimal m_out_abs) {
		this.m_out_abs = m_out_abs;
	}

	public BigDecimal getV_in_abs() {
		return v_in_abs;
	}

	public void setV_in_abs(BigDecimal v_in_abs) {
		this.v_in_abs = v_in_abs;
	}

	public BigDecimal getV_out_abs() {
		return v_out_abs;
	}

	public void setV_out_abs(BigDecimal v_out_abs) {
		this.v_out_abs = v_out_abs;
	}

	public BigDecimal getH_delta_abs() {
		return h_delta_abs;
	}

	public void setH_delta_abs(BigDecimal h_delta_abs) {
		this.h_delta_abs = h_delta_abs;
	}

	public void copyAbsData(ContServiceDataHWater abs) {
		if (abs == null) {
			return;
		}
		this.m_in_abs = abs.getM_in();
		this.m_out_abs = abs.getM_out();
		this.v_in_abs = abs.getV_in();
		this.v_out_abs = abs.getV_out();
		this.h_delta_abs = abs.getH_delta();
	}
}
