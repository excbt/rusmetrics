/**
 * 
 */
package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.excbt.datafuse.nmk.data.model.keyname.ImpulseCounterType;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.10.2016
 * 
 */
public interface ImpulseCounterTypeRepository extends JpaRepository<ImpulseCounterType, String> {

	/**
	 * 
	 * @return
	 */
	@Query("SELECT t FROM ImpulseCounterType t ORDER BY t.orderIdx")
	public List<ImpulseCounterType> selectAllOrdered();
}
