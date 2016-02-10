package ru.excbt.datafuse.nmk.web.api.support;

import org.springframework.data.domain.Page;

/**
 * Версия RequestAnyDataSelector для страничных запросов
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.12.2015
 * 
 * @param <T>
 */
public interface RequestPageDataSelector<T> extends RequestAnyDataSelector<Page<T>> {
}
