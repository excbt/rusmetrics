package ru.excbt.datafuse.nmk.service.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.function.Supplier;

/**
 * Класс для работы полями БД
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 03.08.2015
 */
public class DBRowUtil {

    private DBRowUtil() {

    }

    /**
     * @param arg
     * @return
     */
    public static Long asLong(Object arg) {
        return asLong(arg, () -> new IllegalArgumentException());
    }

    /**
     * @param arg
     * @param exceptionSupplier
     * @param <X>
     * @return
     * @throws X
     */
    public static <X extends Throwable> Long asLong(Object arg, Supplier<? extends X> exceptionSupplier) throws X {
        if (arg == null) {
            return null;
        }
        if (arg instanceof Long) {
            return (Long) arg;

        } else if (arg instanceof Number) {
            long idValue = ((Number) arg).longValue();
            return idValue;
        }
        throw exceptionSupplier.get();
    }

    /**
     * @param arg
     * @return
     */
    public static Integer asInteger(Object arg) {
        Long value = asLong(arg);
        return value.intValue();
    }

    /**
     * @param arg
     * @param exceptionSupplier
     * @param <X>
     * @return
     * @throws X
     */
    public static <X extends Throwable> Integer asInteger(Object arg, Supplier<? extends X> exceptionSupplier) throws X {
        Long value = asLong(arg, exceptionSupplier);
        return value.intValue();
    }

    /**
     * @param arg
     * @return
     */
    public static BigDecimal asBigDecimal(Object arg) {
        return asBigDecimal(arg, () -> new IllegalArgumentException());
    }

    /**
     * @param arg
     * @param exceptionSupplier
     * @param <X>
     * @return
     * @throws X
     */
    public static <X extends Throwable> BigDecimal asBigDecimal(Object arg, Supplier<? extends X> exceptionSupplier) throws X {
        if (arg == null) {
            return null;
        }
        if (arg instanceof BigDecimal) {
            return (BigDecimal) arg;
        }
        if (arg instanceof Double) {
            return new BigDecimal((Double) arg);
        }
        throw exceptionSupplier.get();
    }

    /**
     * @param arg
     * @return
     */
    public static BigInteger asBigInteger(Object arg) {
        return asBigInteger(arg, () -> new IllegalArgumentException());
    }

    /**
     * @param arg
     * @param exceptionSupplier
     * @param <X>
     * @return
     * @throws X
     */
    public static <X extends Throwable> BigInteger asBigInteger(Object arg, Supplier<? extends X> exceptionSupplier) throws X {
        if (arg == null) {
            return null;
        }
        if (arg instanceof BigInteger) {
            return (BigInteger) arg;
        } else
            throw exceptionSupplier.get();
    }

    /**
     * @param arg
     * @return
     */
    public static Double asDouble(Object arg) {
        return asDouble(arg, () -> new IllegalArgumentException());
    }

    /**
     * @param arg
     * @param exceptionSupplier
     * @param <X>
     * @return
     * @throws X
     */
    public static <X extends Throwable> Double asDouble(Object arg, Supplier<? extends X> exceptionSupplier) throws X {
        if (arg == null) {
            return null;
        }
        if (arg instanceof BigDecimal) {
            return ((BigDecimal) arg).doubleValue();
        }
        if (arg instanceof Double) {
            return (Double) arg;
        }
        throw exceptionSupplier.get();
    }

    /**
     * @param arg
     * @return
     */
    public static String asString(Object arg) {
        return asString(arg, () -> new IllegalArgumentException());
    }

    /**
     * @param arg
     * @param exceptionSupplier
     * @param <X>
     * @return
     * @throws X
     */
    public static <X extends Throwable> String asString(Object arg, Supplier<? extends X> exceptionSupplier) throws X {
        if (arg == null) {
            return null;
        }
        if (arg instanceof String) {
            return (String) arg;
        }
        throw exceptionSupplier.get();
    }

    /**
     * @param arg
     * @return
     */
    public static Timestamp asTimestamp(Object arg) {
        return asTimestamp(arg, () -> new IllegalArgumentException());
    }

    /**
     *
     * @param arg
     * @param exceptionSupplier
     * @param <X>
     * @return
     * @throws X
     */
    public static <X extends Throwable> Timestamp asTimestamp(Object arg, Supplier<? extends X> exceptionSupplier) throws X {
        if (arg == null) {
            return null;
        }

        if (arg instanceof Timestamp) {
            return (Timestamp) arg;
        }
        throw exceptionSupplier.get();
    }

    /**
     * @param arg
     * @return
     */
    public static Boolean asBoolean(Object arg) {
        return asBoolean(arg, () -> new IllegalArgumentException());
    }

    /**
     *
     * @param arg
     * @param exceptionSupplier
     * @param <X>
     * @return
     * @throws X
     */
    public static <X extends Throwable> Boolean asBoolean(Object arg, Supplier<? extends X> exceptionSupplier) throws X {
        if (arg == null) {
            return null;
        }
        if (arg instanceof Boolean) {
            return (Boolean) arg;

        }
        throw exceptionSupplier.get();
    }

}
