package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.AuditUser;

public interface AuditUserRepository extends CrudRepository<AuditUser, Long> {

}
