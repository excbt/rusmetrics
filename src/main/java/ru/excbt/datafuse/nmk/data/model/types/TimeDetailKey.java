package ru.excbt.datafuse.nmk.data.model.types;

import ru.excbt.datafuse.nmk.data.domain.KeynameObject;

public enum TimeDetailKey implements KeynameObject {

	TYPE_24H("24h"), TYPE_1H("1h");
	//, TYPE_ABS("abs");

	private final String keyname;
	private final static String ABS_SUFFIX = "_abs";

	private TimeDetailKey(String keyname) {
		this.keyname = keyname;
	}

	public String getKeyname() {
		return this.keyname;
	}

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	public static TimeDetailKey searchKeyname(String keyname) {
		TimeDetailKey result = null;
		for (TimeDetailKey d : TimeDetailKey.values()) {
			if (d.keyname.equals(keyname)) {
				result = d;
				break;
			}
		}
		return result;
	}

	public String getAbsPair() {
		return this.keyname + ABS_SUFFIX;
		
//		if (this == TYPE_ABS) {
//			return "";
//		} else {
//
//			return this.keyname + ABS_SUFFIX;
//		}
	}
}
