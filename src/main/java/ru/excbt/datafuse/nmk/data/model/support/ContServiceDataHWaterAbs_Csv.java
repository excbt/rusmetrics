package ru.excbt.datafuse.nmk.data.model.support;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;

@Getter
@Setter
public class ContServiceDataHWaterAbs_Csv extends ContServiceDataHWater {

	/**
	 *
	 */
	private static final long serialVersionUID = 2942100515881972912L;

	private Double m_in_abs;

	private Double m_out_abs;

	private Double v_in_abs;

	private Double v_out_abs;

	private Double h_delta_abs;

	public ContServiceDataHWaterAbs_Csv() {

	}

	public static ContServiceDataHWaterAbs_Csv newInstance(
			ContServiceDataHWater parent) throws IllegalAccessException,
			InvocationTargetException {
		ContServiceDataHWaterAbs_Csv result = new ContServiceDataHWaterAbs_Csv();
		BeanUtils.copyProperties(result, parent);
		return result;
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
