package ru.excbt.datafuse.nmk.data.model.types;

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;
import java.util.stream.Stream;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

public enum TimeDetailKey implements KeynameObject {

	TYPE_24H("24h", true, true),
	TYPE_1H("1h", false, true),
	TYPE_1DAY("1day", true, true),
	TYPE_1MON("1mon", true, true),
	TYPE_30MIN("30min", false, false),
	TYPE_ABS("abs", false, false);

	private final String keyname;
	private final static String ABS_SUFFIX = "_abs";
	private final boolean truncDate;
	private final boolean haveAbs;

	private TimeDetailKey(String keyname, boolean truncDate, boolean haveAbs) {
		this.keyname = keyname;
		this.truncDate = truncDate;
		this.haveAbs = haveAbs;
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
		checkState(haveAbs);
		return this.keyname + ABS_SUFFIX;
	}

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	public static TimeDetailKey searchKeyname(String keyname) {

		Optional<TimeDetailKey> opt = Stream.of(TimeDetailKey.values()).filter((i) -> i.keyname.equals(keyname))
				.findFirst();

		return opt.isPresent() ? opt.get() : null;
	}

	public boolean isTruncDate() {
		return truncDate;
	}
}
