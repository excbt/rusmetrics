package ru.excbt.datafuse.nmk.data.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class GroupUtil {

    private GroupUtil() {
    }

    /**
     * Make Map from List grouped by id
     *
     * @param inList
     * @param idGetter
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> Map<K, List<V>> makeIdMap(List<V> inList, Function<V, K> idGetter) {
        Map<K, List<V>> resultMap = new HashMap<>();
        for (V m : inList) {
            if (!resultMap.containsKey(idGetter.apply(m))) {
                resultMap.put(idGetter.apply(m), new ArrayList<>());
            }
            resultMap.get(idGetter.apply(m)).add(m);
        }

        return resultMap;
    }


    public static <K,V> Map<K, List<V>> makeIdMap(Stream<V> inStream, Function<V, K> idGetter) {
        Map<K, List<V>> resultMap = new HashMap<>();
        inStream.forEach(m -> {
            if (!resultMap.containsKey(idGetter.apply(m))) {
                resultMap.put(idGetter.apply(m), new ArrayList<>());
            }
            resultMap.get(idGetter.apply(m)).add(m);

        });
        return resultMap;
    }


}
