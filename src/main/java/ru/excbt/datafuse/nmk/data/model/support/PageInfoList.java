package ru.excbt.datafuse.nmk.data.model.support;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.data.domain.Page;

public class PageInfoList<T> extends InfoList<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4693471645308142967L;
	
	private final long totalElements;
	private final int totalPages;

	public PageInfoList(Collection<T> srcCollection) {
		super(srcCollection);
		this.totalElements = srcCollection.size();
		this.totalPages = 1;
	}

	public PageInfoList(Iterator<T> srcIterator) {
		super(srcIterator);
		this.totalElements = this.getObjects().size();
		this.totalPages = 1;
	}

	public PageInfoList(Page<T> srcPage) {
		super(srcPage.iterator());
		this.totalElements = srcPage.getTotalElements();
		this.totalPages = srcPage.getTotalPages();
	}

	public long getTotalElements() {
		return totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ (int) (totalElements ^ (totalElements >>> 32));
		result = prime * result + totalPages;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageInfoList<?> other = (PageInfoList<?>) obj;
		if (totalElements != other.totalElements)
			return false;
		if (totalPages != other.totalPages)
			return false;
		return true;
	}
	
	
}
