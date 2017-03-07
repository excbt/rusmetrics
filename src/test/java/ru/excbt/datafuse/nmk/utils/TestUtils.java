package ru.excbt.datafuse.nmk.utils;

import java.util.List;

/**
 * Created by kovtonyk on 07.03.2017.
 */
public final class TestUtils {

    /**
     *
     * @param a
     * @return
     */
    public static String arrayToString(long[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "";

        StringBuilder b = new StringBuilder();
        for (int i = 0;; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.toString();
            b.append(", ");
        }
    }

    /**
     *
     * @param a
     * @return
     */
    public static String listToString(List<?> a) {
        if (a == null)
            return "null";
        int iMax = a.size() - 1;
        if (iMax == -1)
            return "";

        StringBuilder b = new StringBuilder();
        for (int i = 0;; i++) {
            b.append(a.get(i));
            if (i == iMax)
                return b.toString();
            b.append(", ");
        }
    }

}
