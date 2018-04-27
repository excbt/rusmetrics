package ru.excbt.datafuse.nmk.service.utils;

public class DBEntityUtil {

    private DBEntityUtil() {
    }

    public static <T> void requireNotNull(T entity, Object id, Class<T> clazz) {
        if (entity == null) {
            throw DBExceptionUtil.newEntityNotFoundException(clazz, id);
        }

    }
}
