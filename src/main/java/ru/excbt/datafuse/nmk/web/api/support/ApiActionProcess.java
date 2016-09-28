/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api.support;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.02.2016
 * 
 */
@FunctionalInterface
public interface ApiActionProcess<T> {

	public T processAndReturnResult();

}
