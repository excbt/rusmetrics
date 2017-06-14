package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;

@Getter
@Setter
public class ContServiceTypeInfoART implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4837770584468417110L;

	@JsonIgnore
	private final ContServiceTypeKey contServiceTypeKey;

	private Double absConsValue;
	private Double tempValue;

	public ContServiceTypeInfoART(ContServiceTypeKey contServiceTypeKey) {
		checkNotNull(contServiceTypeKey);
		this.contServiceTypeKey = contServiceTypeKey;
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
