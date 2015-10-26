package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrServiceItem;

public interface SubscrServiceItemRepository extends JpaRepository<SubscrServiceItem, Long> {

	public List<SubscrServiceItem> findByKeyname(String keyname);

}
