package ru.excbt.datafuse.nmk.web.api.support;

import java.util.List;

/**
 * Оболочка для RequestAnyDataSelector
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.12.2015
 * 
 * @param <T>
 */
public interface RequestListDataSelector<T> extends RequestAnyDataSelector<List<T>> {
}
