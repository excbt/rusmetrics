package ru.excbt.datafuse.nmk.web.api.support;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

public abstract class AbtractUserActionLocation<T, K> extends
		AbstractUserActionResult<T> implements UserActionLocation {

	private HttpServletRequest request;

	public AbtractUserActionLocation(T entity, HttpServletRequest request) {
		super(entity);
		this.request = request;
	}

	protected abstract K getLocationId();

	/**
	 * 
	 */
	@Override
	public URI getLocation() {
		checkNotNull(request);
		checkState(getLocationId() != null, "getLocationId is NULL");
		URI location = URI.create(request.getRequestURI() + '/'
				+ getLocationId());

		return location;
	}
}
