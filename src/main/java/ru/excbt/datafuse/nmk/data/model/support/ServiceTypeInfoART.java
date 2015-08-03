package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;
import java.math.BigDecimal;

import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;

public class ServiceTypeInfoART implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4837770584468417110L;

	private final ContServiceTypeKey contServiceTypeKey;
	private BigDecimal absConsValue;
	private BigDecimal relConsValue;
	private BigDecimal tempValue;

	public ServiceTypeInfoART(ContServiceTypeKey contServiceTypeKey) {
		this.contServiceTypeKey = contServiceTypeKey;
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

	public ContServiceTypeKey getContServiceTypeKey() {
		return contServiceTypeKey;
	}

	public String getMeasureUnit() {
		return contServiceTypeKey != null ? contServiceTypeKey.getMeasureUnit()
				.name() : null;
	}

	@Override
	public String toString() {
		return "ServiceTypeART [contServiceTypeKey=" + contServiceTypeKey
				+ ", absConsValue=" + absConsValue + ", relConsValue="
				+ relConsValue + ", tempValue=" + tempValue
				+ ", getMeasureUnit()=" + getMeasureUnit() + "]";
	}



}
