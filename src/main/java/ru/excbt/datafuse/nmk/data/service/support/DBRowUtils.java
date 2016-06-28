package ru.excbt.datafuse.nmk.data.service.support;

import java.math.BigDecimal;

/**
 * Класс для работы полями БД
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 03.08.2015
 *
 */
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
	public static Integer asInteger(Object arg) {
		Long value = asLong(arg);
		return value.intValue();
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
		if (arg instanceof Double) {
			return new BigDecimal((Double) arg);
		}
		throw new IllegalArgumentException();
	}

}
