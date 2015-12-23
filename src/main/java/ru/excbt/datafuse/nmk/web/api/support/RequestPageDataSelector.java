package ru.excbt.datafuse.nmk.web.api.support;

import org.springframework.data.domain.Page;

public interface RequestPageDataSelector<T> extends RequestAnyDataSelector<Page<T>> {
}
