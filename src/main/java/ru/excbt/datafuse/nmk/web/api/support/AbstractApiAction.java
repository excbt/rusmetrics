package ru.excbt.datafuse.nmk.web.api.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Базовый класс для работы с action
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.05.2015
 *
 */
public abstract class AbstractApiAction implements ApiAction {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractApiAction.class);

	public static final Object EMPTY_RESULT = new Object();

	@Override
	public Object getResult() {
		return null;
	}

}
