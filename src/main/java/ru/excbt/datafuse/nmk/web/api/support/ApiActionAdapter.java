package ru.excbt.datafuse.nmk.web.api.support;

/**
 * Базовый класс для работы с action
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.05.2015
 *
 */
@FunctionalInterface
public interface ApiActionAdapter extends ApiAction {

	public static final Object EMPTY_RESULT = new Object();

	@Override
	default Object getResult() {
		return null;
	}

}
