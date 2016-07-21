package ru.excbt.datafuse.nmk.web.api.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

/**
 * Адаптер для EntityApiActionAdapter
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.10.2015
 * 
 * @param <T>
 * @param <K>
 */
public abstract class ApiActionEntityLocationAdapter<T, K> extends ApiActionEntityAdapter<T>
		implements ApiActionLocation {

	private HttpServletRequest request;

	public ApiActionEntityLocationAdapter(T entity, HttpServletRequest request) {
		super(entity);
		this.request = request;
	}

	public ApiActionEntityLocationAdapter(HttpServletRequest request) {
		super(null);
		this.request = request;
	}

	@Override
	public final void process() {
		super.process();
	}

	protected abstract K getLocationId();

	/**
	 * 
	 */
	@Override
	public URI getLocation() {
		checkNotNull(request, "request is NULL");

		URI location = null;
		if (getResult() != null && getLocationId() != null) {
			location = URI.create(request.getRequestURI() + '/' + getLocationId());
		} else {
			location = URI.create(request.getRequestURI());
		}

		return location;
	}
}
