/**
 * 
 */
package ru.excbt.datafuse.nmk.data.domain;

import java.io.Serializable;

/**
 * 
 * @author A.Kovtonyuk 
 * @version 1.0
 * @since 20.02.2017
 * 
 */
public interface ModelIdable<T extends Serializable> {
	T getId();
	void setId(Long id);
}
