package ru.excbt.datafuse.nmk.data.model.types;

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;
import java.util.stream.Stream;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

/**
 * Тип данных: детализация по времени
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.mm.2015
 *
 */
public enum TimeDetailKey implements KeynameObject {

	TYPE_24H("24h", true, true),
	TYPE_1H("1h", false, true),
	TYPE_1DAY("1day", true, true),
	TYPE_1MON("1mon", true, true),
	TYPE_30MIN("30min", false, false),
	TYPE_ABS("abs", false, false),

	TYPE_24H_ABS("24h_abs", true, false),
	TYPE_1H_ABS("1h_abs", false, false),
	TYPE_1DAY_ABS("1day_abs", true, false),
	TYPE_1MON_ABS("1mon_abs", true, false);

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

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	public static TimeDetailKey searchKeynameAbs(String keyname) {

		Optional<TimeDetailKey> opt = Stream.of(TimeDetailKey.values())
				.filter((i) -> i.haveAbs && i.getAbsPair().equals(keyname)).findFirst();

		return opt.isPresent() ? opt.get() : null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isTruncDate() {
		return truncDate;
	}

	public boolean isHaveAbs() {
		return haveAbs;
	}
}
