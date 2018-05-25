package ru.excbt.datafuse.nmk.service;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static BooleanExpression buildSearchCondition(String searchString, Function<String, BooleanExpression> exprBuilder) {
        List<String> searchArray = new ArrayList<>();
        searchArray.addAll(Arrays.asList(searchString.split("\\s+")));

        BooleanExpression result = null;

        for (String splitString: searchArray) {
            BooleanExpression sExpr = exprBuilder.apply(splitString);
            result = result != null ? result.and(sExpr) : sExpr;
        }

        return result != null ? result : exprBuilder.apply(searchString);
    };

}
