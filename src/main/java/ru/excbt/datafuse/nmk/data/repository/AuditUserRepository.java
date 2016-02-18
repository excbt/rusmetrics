package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.AuditUser;

/**
 * Repository для AuditUser
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.03.2015
 *
 */
public interface AuditUserRepository extends JpaRepository<AuditUser, Long> {

	public List<AuditUser> findByUserName(String userName);

}
