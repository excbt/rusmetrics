package ru.excbt.datafuse.nmk.service;

import java.util.List;
import java.util.function.Function;

public class QueryDSLUtil {

    private QueryDSLUtil() {
    }

    public static final Function<String, String> upperCaseLikeStr = (s) -> '%' + s.toUpperCase() + '%';
    public static final Function<String, String> lowerCaseLikeStr = (s) -> '%' + s.toLowerCase() + '%';

    /**
     *
     * @param arg
     * @return
     */
    public static int getCountValue(List<Long> arg) {
        if (arg == null) return 0;
        if (arg.isEmpty()) return 0;
        if (arg.get(0) == null) return 0;
        return arg.get(0).intValue();
    }

}
