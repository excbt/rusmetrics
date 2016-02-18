package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrServiceItem;

/**
 * Repository для SubscrServiceItem
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.09.2015
 *
 */
public interface SubscrServiceItemRepository extends JpaRepository<SubscrServiceItem, Long> {

	public List<SubscrServiceItem> findByKeyname(String keyname);

}
