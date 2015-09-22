package ru.excbt.datafuse.nmk.data.model.types;

import java.util.Optional;
import java.util.stream.Stream;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

public enum ContServiceTypeKey implements KeynameObject {

	CW(MeasureUnit.V_M3), EL, ENV, GAS, HEAT(MeasureUnit.W_GCAL), HW(
			MeasureUnit.V_M3);

	private final MeasureUnit measureUnit;

	private ContServiceTypeKey(MeasureUnit measureUnit) {
		this.measureUnit = measureUnit;
	}

	private ContServiceTypeKey() {
		this.measureUnit = null;
	}

	/**
	 * 
	 */
	@Override
	public String getKeyname() {
		return this.name().toLowerCase();
	}

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	public static ContServiceTypeKey searchKeyname(String keyname) {

		Optional<ContServiceTypeKey> opt = Stream
				.of(ContServiceTypeKey.values())
				.filter((i) -> i.getKeyname().equals(keyname)).findFirst();

		return opt.isPresent() ? opt.get() : null;
	}

	public MeasureUnit getMeasureUnit() {
		return measureUnit;
	}

}
