package ru.excbt.datafuse.nmk.data.service.support;

import java.util.Arrays;
import java.util.List;

public final class RepositoryUtils {

    private RepositoryUtils() {
    }

    public static <T> List<T> safeList(List<T> inList) {
        if (inList.isEmpty()) inList.add(null);
        return inList;
    }

    public static <T> List<T> safeList(T ... inList) {
        return safeList(Arrays.asList(inList));
    }
}
