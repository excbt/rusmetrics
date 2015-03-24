package ru.excbt.datafuse.nmk.data.constant;

public enum TimeDetail {

	TYPE_24H("24h"), TYPE_1H("1h");

	private final String keyname;

	private TimeDetail(String keyname) {
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
	public static TimeDetail searchKeyname(String keyname) {
		TimeDetail result = null;
		for (TimeDetail d : TimeDetail.values()) {
			if (d.keyname.equals(keyname)) {
				result = d;
				break;
			}
		}
		return result;
	}
}
