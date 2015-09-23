package ru.excbt.datafuse.nmk.data.model.types;

import java.util.Optional;
import java.util.stream.Stream;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

public enum TimeDetailKey implements KeynameObject {

	TYPE_24H("24h", true), TYPE_1H("1h", false);

	private final String keyname;
	private final static String ABS_SUFFIX = "_abs";
	private final boolean truncDate;

	private TimeDetailKey(String keyname, boolean truncDate) {
		this.keyname = keyname;
		this.truncDate = truncDate;
	}

	@Override
	public String getKeyname() {
		return this.keyname;
	}

	/**
	 * 
	 * @return
	 */
	public String getAbsPair() {
		return this.keyname + ABS_SUFFIX;
	}

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	public static TimeDetailKey searchKeyname(String keyname) {

		Optional<TimeDetailKey> opt = Stream.of(TimeDetailKey.values())
				.filter((i) -> i.keyname.equals(keyname)).findFirst();

		return opt.isPresent() ? opt.get() : null;
	}

	public boolean isTruncDate() {
		return truncDate;
	}
}
