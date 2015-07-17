package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.AuditUser;

public interface AuditUserRepository extends JpaRepository<AuditUser, Long> {

	public List<AuditUser> findByUserName(String userName);

}
