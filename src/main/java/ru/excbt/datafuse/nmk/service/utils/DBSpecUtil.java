package ru.excbt.datafuse.nmk.service.utils;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import java.util.List;

public class DBSpecUtil {

    private DBSpecUtil() {
    }

    public static final  <T> Specifications<T> specsAndFilterBuild(List<Specification<T>> specList) {
        if (specList == null) {
            return null;
        }
        Specifications<T> result = null;
        for (Specification<T> i : specList) {
            if (i == null) {
                continue;
            }
            result = result == null ? Specifications.where(i) : result.and(i);
        }

        return result;
    }

}
