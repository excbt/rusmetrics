package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.math.BigDecimal;

import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;

public class ContServiceTypeInfoART implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4837770584468417110L;

	@JsonIgnore
	private final ContServiceTypeKey contServiceTypeKey;

	private BigDecimal absConsValue;
	private BigDecimal tempValue;

	public ContServiceTypeInfoART(ContServiceTypeKey contServiceTypeKey) {
		checkNotNull(contServiceTypeKey);
		this.contServiceTypeKey = contServiceTypeKey;
	}

	public BigDecimal getAbsConsValue() {
		return absConsValue;
	}

	public void setAbsConsValue(BigDecimal absConsValue) {
		this.absConsValue = absConsValue;
	}

	public BigDecimal getTempValue() {
		return tempValue;
	}

	public void setTempValue(BigDecimal tempValue) {
		this.tempValue = tempValue;
	}

	public String getContServiceType() {
		return contServiceTypeKey.getKeyname();
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
		return MoreObjects.toStringHelper(ContServiceTypeInfoART.class)
				.add("contServiceTypeKey", contServiceTypeKey)
				.add("absConsValue", absConsValue).add("tempValue", tempValue)
				.add("getMeasureUnit()", getMeasureUnit()).toString();
	}

}
