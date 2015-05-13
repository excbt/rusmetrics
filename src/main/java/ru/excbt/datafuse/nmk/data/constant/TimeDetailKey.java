package ru.excbt.datafuse.nmk.data.constant;

public enum TimeDetailKey {

	TYPE_24H("24h"), TYPE_1H("1h"), TYPE_ABS ("abs");

	private final String keyname;

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
}
