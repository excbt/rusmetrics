/**
 * 
 */
package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrServicePackSpecial;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 08.11.2016
 * 
 */
public interface SubscrServicePackSpecialRepository extends JpaRepository<SubscrServicePackSpecial, Long> {

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public List<SubscrServicePackSpecial> findBySubscriberId(Long subscriberId);
}
