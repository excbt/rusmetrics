package ru.excbt.datafuse.nmk.utils;

public interface ValidPeriodCheckable {

    /**
     *
     * @return
     */
    boolean isValid();

    /**
     *
     * @return
     */
    boolean isValidEq();

    /**
     *
     * @return
     */
    default boolean isInvalid() {
        return !isValid();
    }

    /**
     *
     * @return
     */
    default boolean isInvalidEq() {
        return !isValidEq();
    }

}
