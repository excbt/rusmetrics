package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ContEvent;

public interface ContEventRepository extends CrudRepository<ContEvent, Long> {

	public List<ContEvent> findByContObjectId(long id);
}
