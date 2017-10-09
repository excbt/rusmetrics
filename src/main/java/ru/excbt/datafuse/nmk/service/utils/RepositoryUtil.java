package ru.excbt.datafuse.nmk.service.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class RepositoryUtil {

    public final static List<Long> NO_DATA_IDS = Collections.unmodifiableList(Arrays.asList(Long.MIN_VALUE));

    private RepositoryUtil() {
    }

    public static <T> List<T> safeList(List<T> inList) {
        if (inList.isEmpty()) inList.add(null);
        return inList;
    }

    public static <T> List<T> safeList(T ... inList) {
        return safeList(Arrays.asList(inList));
    }
}
