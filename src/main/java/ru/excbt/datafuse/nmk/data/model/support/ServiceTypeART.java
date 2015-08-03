package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;
import java.math.BigDecimal;

public class ServiceTypeART implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4837770584468417110L;
	
	
	private final String serviceTypeKeyname;
	private BigDecimal absConsValue;
	private BigDecimal relConsValue;
	private BigDecimal tempValue;

	public ServiceTypeART(String serviceTypeKeyname) {
		this.serviceTypeKeyname = serviceTypeKeyname;
	}

	public BigDecimal getAbsConsValue() {
		return absConsValue;
	}

	public void setAbsConsValue(BigDecimal absConsValue) {
		this.absConsValue = absConsValue;
	}

	public BigDecimal getRelConsValue() {
		return relConsValue;
	}

	public void setRelConsValue(BigDecimal relConsValue) {
		this.relConsValue = relConsValue;
	}

	public BigDecimal getTempValue() {
		return tempValue;
	}

	public void setTempValue(BigDecimal tempValue) {
		this.tempValue = tempValue;
	}

	public String getServiceTypeKeyname() {
		return serviceTypeKeyname;
	}
}
