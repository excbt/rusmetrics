package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.ContObject;

public interface ContObjectRepository extends JpaRepository<ContObject, Long> {

	public List<ContObject> findByFullNameLikeIgnoreCase(String str);
}
