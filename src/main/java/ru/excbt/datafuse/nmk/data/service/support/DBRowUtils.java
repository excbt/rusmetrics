package ru.excbt.datafuse.nmk.data.service.support;

import java.math.BigDecimal;
import java.sql.Timestamp;

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

	public static Double asDouble(Object arg) {
		if (arg == null) {
			return null;
		}
		if (arg instanceof BigDecimal) {
			return ((BigDecimal) arg).doubleValue();
		}
		if (arg instanceof Double) {
			return (Double) arg;
		}
		throw new IllegalArgumentException();
	}

	/**
	 *
	 * @param arg
	 * @return
	 */
	public static String asString(Object arg) {
		if (arg instanceof String) {
			return (String) arg;
		}
		throw new IllegalArgumentException();
	}

	/**
	 *
	 * @param arg
	 * @return
	 */
	public static Timestamp asTimestamp(Object arg) {
		if (arg instanceof Timestamp) {
			return (Timestamp) arg;
		}
		throw new IllegalArgumentException();
	}

	/**
	 *
	 * @param arg
	 * @return
	 */
	public static Boolean asBoolean(Object arg) {
		if (arg == null) {
			return null;
		}
		if (arg instanceof Boolean) {
			return (Boolean) arg;

		}
		throw new IllegalArgumentException();
	}

}
