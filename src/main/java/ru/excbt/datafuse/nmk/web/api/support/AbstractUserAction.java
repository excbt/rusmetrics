package ru.excbt.datafuse.nmk.web.api.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUserAction implements UserAction {

	protected static final Logger logger = LoggerFactory
			.getLogger(AbstractUserAction.class);

	protected static final Object EMPTY_RESULT = new Object();

	@Override
	public Object getResult() {
		return EMPTY_RESULT;
	}

}
