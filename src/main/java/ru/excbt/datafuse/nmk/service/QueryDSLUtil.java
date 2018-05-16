package ru.excbt.datafuse.nmk.service;

import java.util.function.Function;

public class QueryDSLUtil {

    private QueryDSLUtil() {
    }

    public static final Function<String, String> upperCaseLikeStr = (s) -> '%' + s.toUpperCase() + '%';
    public static final Function<String, String> lowerCaseLikeStr = (s) -> '%' + s.toLowerCase() + '%';

}
