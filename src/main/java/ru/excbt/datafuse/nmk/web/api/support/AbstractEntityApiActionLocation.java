package ru.excbt.datafuse.nmk.web.api.support;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractEntityApiActionLocation<T, K> extends
		AbstractEntityApiAction<T> implements ApiActionLocation {

	private HttpServletRequest request;

	public AbstractEntityApiActionLocation(T entity, HttpServletRequest request) {
		super(entity);
		this.request = request;
	}

	protected abstract K getLocationId();

	/**
	 * 
	 */
	@Override
	public URI getLocation() {
		checkNotNull(request, "request is NULL");
		
		URI location = null;
		if (getResult() != null) {
			location = URI.create(request.getRequestURI() + '/'
					+ getLocationId());
		} else {
			location = URI.create(request.getRequestURI());
		}

		return location;
	}
}
