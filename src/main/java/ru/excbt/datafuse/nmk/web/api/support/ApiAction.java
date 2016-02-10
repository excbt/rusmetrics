package ru.excbt.datafuse.nmk.web.api.support;

/**
 * Интерфейс для работы с action
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.05.2015
 *
 */
public interface ApiAction {
	public void process();

	public Object getResult();
}
