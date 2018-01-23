package ru.excbt.datafuse.nmk.data.model.types;

import java.util.Optional;
import java.util.stream.Stream;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

/**
 * Тип данных: Тип сервиса системы
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 03.08.2015
 *
 */
public enum ContServiceTypeKey implements KeynameObject {

	CW(MeasureUnitKey.V_M3), EL, ENV, GAS, HEAT(MeasureUnitKey.W_GCAL), HW(MeasureUnitKey.V_M3);

	private final MeasureUnitKey measureUnit;

	private ContServiceTypeKey(MeasureUnitKey measureUnit) {
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

		Optional<ContServiceTypeKey> opt = Stream.of(ContServiceTypeKey.values())
				.filter((i) -> i.getKeyname().equals(keyname)).findFirst();

		return opt.isPresent() ? opt.get() : null;
	}

	public MeasureUnitKey getMeasureUnit() {
		return measureUnit;
	}

}
