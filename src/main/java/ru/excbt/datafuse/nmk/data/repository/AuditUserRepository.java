package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.AuditUser;

public interface AuditUserRepository extends CrudRepository<AuditUser, Long> {

	public List<AuditUser> findByUserName(String userName);

}
