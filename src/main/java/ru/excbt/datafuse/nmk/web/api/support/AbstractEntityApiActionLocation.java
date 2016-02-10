package ru.excbt.datafuse.nmk.web.api.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

/**
 * Расширенный класс для работы action с сущностью при POST
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.05.2015
 * 
 * @param <T>
 * @param <K>
 */
@Deprecated
public abstract class AbstractEntityApiActionLocation<T, K> extends AbstractEntityApiAction<T>
		implements ApiActionLocation {

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
			location = URI.create(request.getRequestURI() + '/' + getLocationId());
		} else {
			location = URI.create(request.getRequestURI());
		}

		return location;
	}
}
