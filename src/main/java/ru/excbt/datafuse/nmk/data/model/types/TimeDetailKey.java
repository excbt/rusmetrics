package ru.excbt.datafuse.nmk.data.model.types;

import java.util.Optional;
import java.util.stream.Stream;

import ru.excbt.datafuse.nmk.data.domain.KeynameObject;

public enum TimeDetailKey implements KeynameObject {

	TYPE_24H("24h"), TYPE_1H("1h");

	private final String keyname;
	private final static String ABS_SUFFIX = "_abs";

	private TimeDetailKey(String keyname) {
		this.keyname = keyname;
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
}
