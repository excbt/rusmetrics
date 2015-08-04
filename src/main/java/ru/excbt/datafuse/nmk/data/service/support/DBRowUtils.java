package ru.excbt.datafuse.nmk.data.service.support;

import java.math.BigDecimal;

public class DBRowUtils {

	private DBRowUtils() {

	}

	/**
	 * 
	 * @param arg
	 * @return
	 */
	public static Long asLong(Object arg) {
		if (arg == null) {
			return null;
		}
		if (arg instanceof Long) {
			return (Long) arg;

		} else if (arg instanceof Number) {

			long idValue = ((Number) arg).longValue();
			return idValue;

		}
		throw new IllegalArgumentException();
	}

	/**
	 * 
	 * @param arg
	 * @return
	 */
	public static BigDecimal asBigDecimal(Object arg) {
		if (arg == null) {
			return null;
		}
		if (arg instanceof BigDecimal) {
			return (BigDecimal) arg;
		}
		throw new IllegalArgumentException();
	}

}
