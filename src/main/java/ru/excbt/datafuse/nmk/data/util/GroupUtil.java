package ru.excbt.datafuse.nmk.data.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GroupUtil {

    private GroupUtil() {
    }

    /**
     * Make Map from List grouped by id
     * @param inList
     * @param idGetter
     * @param <T>
     * @return
     */
    public static <T> Map<Long, List<T>> makeIdMap(List<T> inList, Function<T, Long> idGetter) {
        Map<Long, List<T>> resultMap = new HashMap<>();
        for (T m : inList) {
            if (!resultMap.containsKey(idGetter.apply(m))) {
                resultMap.put(idGetter.apply(m), new ArrayList<>());
            }
            resultMap.get(idGetter.apply(m)).add(m);
        }

        return resultMap;
    }

}
