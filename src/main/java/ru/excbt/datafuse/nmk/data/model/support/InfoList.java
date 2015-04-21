package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

public class InfoList<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6715720681226889272L;
	
	private final List<T> objects;

	public InfoList(Collection<T> srcObjects) {
		checkNotNull(srcObjects);
		this.objects = Collections.unmodifiableList(Lists
				.newArrayList(srcObjects));
	}

	public InfoList(Iterator<T> srcObjects) {
		checkNotNull(srcObjects);
		this.objects = Collections.unmodifiableList(Lists
				.newArrayList(srcObjects));
	}

	public List<T> getObjects() {
		return objects;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objects == null) ? 0 : objects.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InfoList<?> other = (InfoList<?>) obj;
		if (objects == null) {
			if (other.objects != null)
				return false;
		} else if (!objects.equals(other.objects))
			return false;
		return true;
	}

}
